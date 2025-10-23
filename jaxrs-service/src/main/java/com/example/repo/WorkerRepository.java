package com.example.repo;


import com.example.model.ErrorResponse;
import com.example.model.Organization;
import com.example.model.SearchCriteria;
import com.example.model.Worker;
import com.example.service.WorkerSpecification;
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
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WorkerRepository {

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


    public List<Worker> searchByCriteria(SearchCriteria searchCriteria, int page, int size) {
        List<String> sort = searchCriteria.getSort();

        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Worker> criteriaQuery = criteriaBuilder.createQuery(Worker.class);
        Root<Worker> root = criteriaQuery.from(Worker.class);

        if (searchCriteria.getFilter() != null && !searchCriteria.getFilter().isEmpty()) {
            WorkerSpecification workerSpecification = new WorkerSpecification(searchCriteria.getFilter());
            criteriaQuery.where(workerSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
        }

        if (searchCriteria.getSort() != null && !searchCriteria.getSort().isEmpty()) {
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
            criteriaQuery.orderBy(orders);
        }
        TypedQuery<Worker> workers = entityManager
                .createQuery(criteriaQuery)
                .setFirstResult((page - 1) * size)
                .setMaxResults(size);
        return workers.getResultList();
    }

    public Long countByCriteria(SearchCriteria searchCriteria) {
        CriteriaBuilder criteriaBuilder = entityManager.getCriteriaBuilder();
        CriteriaQuery<Long> criteriaQuery = criteriaBuilder.createQuery(Long.class);
        Root<Worker> root = criteriaQuery.from(Worker.class);
        criteriaQuery.select(criteriaBuilder.count(root));
        if (searchCriteria.getFilter() != null && !searchCriteria.getFilter().isEmpty()) {
            WorkerSpecification workerSpecification = new WorkerSpecification(searchCriteria.getFilter());
            criteriaQuery.where(workerSpecification.toPredicate(root, criteriaQuery, criteriaBuilder));
        }
        return entityManager.createQuery(criteriaQuery).getSingleResult();
    }


}
