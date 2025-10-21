package com.example.repo;


import com.example.model.Organization;
import com.example.model.Worker;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;

import java.util.List;

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

    public Long getTotalRecords() {
        return entityManager.createQuery("SELECT COUNT(w) FROM Worker w", Long.class).getSingleResult();
    }

}
