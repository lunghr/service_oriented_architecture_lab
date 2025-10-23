package com.example.service;

import com.example.model.Position;
import com.example.model.Status;
import com.example.model.Worker;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.persistence.criteria.CriteriaQuery;
import jakarta.persistence.criteria.Predicate;
import jakarta.persistence.criteria.Root;

import java.time.LocalDate;
import java.util.*;

public class WorkerSpecification {

    private final List<String> filerList;
    private final Map<String, Object> filter;

    public WorkerSpecification(List<String> filterList) {
        this.filerList = filterList;
        this.filter = new HashMap<>();
    }

    public Predicate toPredicate(Root<Worker> root, CriteriaQuery<?> query, CriteriaBuilder criteriaBuilder) {
        List<Predicate> predicates = new ArrayList<>();
        format();
        for (String key : filter.keySet()) {
            try {
                switch (key) {
                    case "organization":
                        predicates.add(criteriaBuilder.like(root.get(key).get("id"), filter.get(key).toString()));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get(key), filter.get(key).toString()));
                        break;
                    default:
                        predicates.add(criteriaBuilder.equal(root.get(key), filter.get(key)));
                        break;
                }
            } catch (Exception ignored) {
            }
        }
        return criteriaBuilder.and(predicates.toArray(new Predicate[0]));
    }

    private void format() {
        Map<String, String> unformattedFilter = filerList.stream().map(s -> s.split(":", 2))
                .filter(arr -> arr.length == 2)
                .collect(HashMap::new, (m, a) -> m.put(a[0], a[1]), HashMap::putAll);
        for (String key : unformattedFilter.keySet()) {
            try {
                switch (key) {
                    case "salary", "x", "y":
                        filter.put(key, Long.parseLong(unformattedFilter.get(key)));
                        break;
                    case "name", "organization":
                        filter.put(key, "%" + unformattedFilter.get(key) + "%");
                        break;
                    case "startDate":
                        filter.put(key, LocalDate.parse(unformattedFilter.get(key)));
                        break;
                    case "status":
                        filter.put(key, Status.valueOf(unformattedFilter.get(key).toUpperCase()));
                        break;
                    case "position":
                        filter.put(key, Position.valueOf(unformattedFilter.get(key).toUpperCase()));
                        break;
                }
            } catch (Exception ignored) {
            }
        }
    }
}