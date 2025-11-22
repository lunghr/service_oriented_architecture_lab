package com.example.service;

import com.example.model.Position;
import com.example.model.Status;
import com.example.model.Worker;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;
import jakarta.ws.rs.BadRequestException;

import java.time.LocalDate;
import java.util.*;

public class WorkerSpecification {

    private final List<String> filterList;
    private final Map<String, Object> filter = new HashMap<>();
    private final Map<String, String> operators = new HashMap<>();

    public WorkerSpecification(List<String> filterList) {
        this.filterList = filterList;
    }

    public Predicate toPredicate(Root<Worker> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        format();
        for (String key : filter.keySet()) {
            try {
                String op = operators.getOrDefault(key, "eq");
                Object value = filter.get(key);
                switch (key) {
                    case "organization":
                        if ("like".equals(op)) {
                            predicates.add(criteriaBuilder.like(root.get(key).get("id"), value.toString()));
                        } else {
                            predicates.add(criteriaBuilder.equal(root.get(key).get("id"), value));
                        }
                        break;
                    case "name":
                        predicates.add(buildStringPredicate(root, criteriaBuilder, key, op, value));
                        break;
                    default:
                        predicates.add(buildGenericPredicate(root, criteriaBuilder, key, op, value));
                        break;
                }
            } catch (Exception e) {
                throw new BadRequestException("Invalid filter for key: " + key + " - " + e.getMessage());
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private Predicate buildStringPredicate(Root<Worker> root, CriteriaBuilder cb, String key, String op, Object value) {
        return switch (op) {
            case "like" -> cb.like(cb.lower(root.get(key)), "%" + value.toString().toLowerCase() + "%");
            case "eq" -> cb.equal(cb.lower(root.get(key)), value.toString().toLowerCase());
            default -> throw new IllegalArgumentException("Unsupported op for string: " + op);
        };
    }


    private Predicate buildGenericPredicate(Root<Worker> root, CriteriaBuilder cb, String key, String op, Object value) {
        return switch (op) {
            case "eq" -> {
                if (value instanceof String) {
                    yield cb.equal(cb.lower(root.get(key)), value.toString().toLowerCase());
                }
                yield cb.equal(root.get(key), value);
            }
            case "gt" -> cb.greaterThan(root.get(key), (Comparable) value);
            case "lt" -> cb.lessThan(root.get(key), (Comparable) value);
            case "gte" -> cb.greaterThanOrEqualTo(root.get(key), (Comparable) value);
            case "lte" -> cb.lessThanOrEqualTo(root.get(key), (Comparable) value);
            case "in" -> {
                if (!(value instanceof List<?> list)) throw new IllegalArgumentException("Value for 'in' must be list");
                yield root.get(key).in(list);
            }
            case "between" -> {
                if (!(value instanceof List<?> list) || list.size() != 2)
                    throw new IllegalArgumentException("Value for 'between' must be list of 2 elements");
                Comparable<Object> from = (Comparable<Object>) list.get(0);
                Comparable<Object> to = (Comparable<Object>) list.get(1);
                yield cb.between(root.get(key), from, to);
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
                throw new BadRequestException("Invalid filter format: " + s + " - " + e.getMessage());
            }
        }
    }

    private Object parseValue(String key, String rawValue) {
        return switch (key) {
            case "salary", "x", "y" -> Long.parseLong(rawValue);
            case "name", "organization" -> "%" + rawValue + "%";
            case "startDate" -> LocalDate.parse(rawValue);
            case "status" -> Status.valueOf(rawValue.toUpperCase());
            case "position" -> Position.valueOf(rawValue.toUpperCase());
            default -> rawValue;
        };
    }
}