package com.example.repo;

import com.example.model.Organization;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.transaction.Transactional;

@ApplicationScoped
public class OrganizationRepository {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager entityManager;

    @Transactional
    public void save (Organization organization){
        Organization org = entityManager.find(Organization.class, organization.getFullName());
        if (org == null){
            entityManager.persist(organization);
        } else {
            updateNonNullFields(org, organization);
            entityManager.merge(org);
        }
    }

    @Transactional
    public Organization findByFullName(String fullName){
        return entityManager.find(Organization.class, fullName);
    }

    private void updateNonNullFields(Organization target, Organization source) {
        if (source.getEmployeesCount() != null) {
            target.setEmployeesCount(source.getEmployeesCount());
        }
        if (source.getOrganizationType() != null) {
            target.setOrganizationType(source.getOrganizationType());
        }
        if (source.getOfficialAddress() != null) {
            target.setOfficialAddress(source.getOfficialAddress());
        }
    }







}
