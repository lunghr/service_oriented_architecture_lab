package com.example.service;

import com.example.model.Worker;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.domain.Specification;
import jakarta.persistence.criteria.*;
import java.util.*;

public class WorkerSpecification implements Specification<@NonNull Worker> {
    private final List<String> filterList;
    private final Map<String, Object> filter = new HashMap<>();
    private final Map<String, String> operators = new HashMap<>();

    public WorkerSpecification(List<String> filterList) {
        this.filterList = filterList;
        format();
    }

    @Override
    public Predicate toPredicate(Root<Worker> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();

        for (String key : filter.keySet()) {
            try {
                String op = operators.getOrDefault(key, "eq");
                Object value = filter.get(key);

                switch (key) {
                    case "organization":
                        predicates.add(buildOrganizationPredicate(root, criteriaBuilder, op, value));
                        break;
                    case "name":
                        predicates.add(buildStringPredicate(root, criteriaBuilder, key, op, value));
                        break;
                    default:
                        predicates.add(buildGenericPredicate(root, criteriaBuilder, key, op, value));
                }
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid filter for key: " + key + " - " + e.getMessage());
            }
        }

        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildOrganizationPredicate(Root<Worker> root, CriteriaBuilder cb, String op, Object value) {
        String val = value.toString().replace("%", "");
        return "like".equals(op)
                ? cb.like(root.get("organization").get("id").as(String.class), "%" + val + "%")
                : cb.equal(root.get("organization").get("id"), Long.parseLong(val));
    }

    private Predicate buildStringPredicate(Root<Worker> root, CriteriaBuilder cb, String key, String op, Object value) {
        String val = value.toString().replace("%", "");
        return switch (op) {
            case "like" -> cb.like(cb.lower(root.get(key)), "%" + val.toLowerCase() + "%");
            case "eq" -> cb.equal(cb.lower(root.get(key)), val.toLowerCase());
            default -> throw new IllegalArgumentException("Unsupported op for string: " + op);
        };
    }

    private Predicate buildGenericPredicate(Root<Worker> root, CriteriaBuilder cb, String key, String op, Object value) {
        return switch (op) {
            case "eq" -> cb.equal(root.get(key), value);
            case "gt" -> cb.greaterThan(root.get(key), (Comparable) value);
            case "lt" -> cb.lessThan(root.get(key), (Comparable) value);
            case "gte" -> cb.greaterThanOrEqualTo(root.get(key), (Comparable) value);
            case "lte" -> cb.lessThanOrEqualTo(root.get(key), (Comparable) value);
            case "in" -> root.get(key).in((List<?>) value);
            case "between" -> {
                List<?> list = (List<?>) value;
                yield cb.between(root.get(key), (Comparable) list.get(0), (Comparable) list.get(1));
            }
            default -> throw new IllegalArgumentException("Unsupported operator: " + op);
        };
    }

    private void format() {
        for (String s : filterList) {
            try {
                String[] parts = s.split(":", -1);
                if (parts.length < 2 || parts.length > 4) continue;

                String key = parts[0].trim();
                String op = (parts.length >= 3) ? parts[1].trim().toLowerCase() : "eq";
                Object value;

                if (parts.length == 2) {
                    value = parseValue(key, parts[1].trim());
                } else if (parts.length == 3) {
                    value = parseValue(key, parts[2].trim());
                } else {
                    List<Object> list = new ArrayList<>();
                    for (int i = 2; i < parts.length; i++) {
                        list.add(parseValue(key, parts[i].trim()));
                    }
                    value = list;
                }

                filter.put(key, value);
                operators.put(key, op);
            } catch (Exception e) {
                throw new IllegalArgumentException("Invalid filter format: " + s + " - " + e.getMessage());
            }
        }
    }

    private Object parseValue(String key, String rawValue) {
        return switch (key) {
            case "salary", "x", "y" -> Long.parseLong(rawValue);
            case "name", "organization" -> "%" + rawValue + "%";
            case "startDate" -> java.time.LocalDate.parse(rawValue);
            case "status" -> com.example.model.Status.valueOf(rawValue.toUpperCase());
            case "position" -> com.example.model.Position.valueOf(rawValue.toUpperCase());
            default -> rawValue;
        };
    }
}
