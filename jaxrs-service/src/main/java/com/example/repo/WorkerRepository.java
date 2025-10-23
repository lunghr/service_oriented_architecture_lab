package com.example.repo;


import com.example.model.ErrorResponse;
import com.example.model.Organization;
import com.example.model.Worker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.EntityNotFoundException;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.persistence.criteria.*;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WorkerRepository {

    private final static Logger logger = LoggerFactory.getLogger(WorkerRepository.class);

    @Inject
    private OrganizationRepository organizationRepository;

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Transactional
    public void save(Worker worker) {
        organizationRepository.save(worker.getOrganization());
        Organization organization = organizationRepository.findByFullName(worker.getOrganization().getFullName());
        worker.setOrganization(organization);

        if (worker.getId() != null) {
            entityManager.persist(worker);
        }
        entityManager.merge(worker);
    }

    public List<Worker> findWithPagination(int page, int size) {
        TypedQuery<Worker> query = entityManager.createQuery("SELECT w FROM Worker w", Worker.class)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size);
        return query.getResultList();
    }


    public Worker findById(Long id) {
        return entityManager.find(Worker.class, id);
    }

    @Transactional
    public void delete(Long id) {
        try {
            Worker worker = entityManager.getReference(Worker.class, id);
            entityManager.remove(worker);
        } catch (EntityNotFoundException e) {
            throw new NotFoundException(Response.status(404).entity(
                    ErrorResponse.builder()
                            .code(404)
                            .message("⚠️ Worker with id " + id + " not found.")
                            .build()
            ).build());
        }
    }

    public List<Worker> findByStartDate(LocalDate date) {
        TypedQuery<Worker> query = entityManager.createQuery(
                "SELECT w FROM Worker w WHERE w.startDate = :startDate", Worker.class);
        query.setParameter("startDate", date);
        return query.getResultList();
    }

    public Worker findWorkerWithMinSalary() {
        List<Worker> resultList = entityManager.createQuery("SELECT w FROM Worker w WHERE w.salary IS NOT NULL ORDER BY w.salary ASC", Worker.class).setMaxResults(1).getResultList();
        return resultList.isEmpty() ? null : resultList.getFirst();
    }

    public int countByStartDateBefore(LocalDate date) {
        TypedQuery<Long> query = entityManager.createQuery(
                "SELECT COUNT(w) FROM Worker w WHERE w.startDate < :startDate", Long.class);
        query.setParameter("startDate", date);
        return query.getSingleResult().intValue();
    }

    public List<Worker> sortByCriteria(List<String> sort) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Worker> criteriaQuery = criteriaBuilder.createQuery(Worker.class);
        Root<Worker> root = criteriaQuery.from(Worker.class);
        List<Order> orders = new ArrayList<>();
        for (String s : sort) {
            try {
                if (s.startsWith("-")) {
                    orders.add(criteriaBuilder.desc(root.get(s.substring(1))));
                } else {
                    orders.add(criteriaBuilder.asc(root.get(s)));
                }
            } catch (IllegalArgumentException ignored) {
            }
        }
        criteriaQuery.select(root);
        criteriaQuery.orderBy(orders);
        TypedQuery<Worker> workers = entityManager.createQuery(criteriaQuery);
        return workers.getResultList();
    }

    public List<Worker> filterByCriteria(Map<String, String> filter) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Worker> criteriaQuery = criteriaBuilder.createQuery(Worker.class);
        Root<Worker> root = criteriaQuery.from(Worker.class);
        List<Predicate> predicates = new ArrayList<>();
        for (String key : filter.keySet()) {
            try {
                switch (key) {
                    case "position", "status":
                        predicates.add(criteriaBuilder.equal(root.get(key), filter.get(key)));
                        break;
                    case "organization":
                        predicates.add(criteriaBuilder.like(root.get(key).get("id"), "%" + filter.get(key) + "%"));
                        break;
                    case "name":
                        predicates.add(criteriaBuilder.like(root.get(key), "%" + filter.get(key) + "%"));
                        break;
                    case "startDate":
                        predicates.add(criteriaBuilder.equal(root.get(key), LocalDate.parse(filter.get(key))));
                        break;
                    case "salary", "x", "y":
                        predicates.add(criteriaBuilder.equal(root.get(key), Long.parseLong(filter.get(key))));
                        break;
                }
            } catch (Exception ignored) {
            }
        }
        criteriaQuery.where(predicates.toArray(new Predicate[0]));
        TypedQuery<Worker> query = entityManager.createQuery(criteriaQuery);
        return query.getResultList();
    }


}
