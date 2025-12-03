package com.example.service;

import com.example.model.Organization;
import com.example.repo.OrganizationRepository;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
public class OrganizationService {

    private final OrganizationRepository organizationRepository;

    public OrganizationService(OrganizationRepository organizationRepository) {
        this.organizationRepository = organizationRepository;
    }

    @Transactional
    public Organization saveOrUpdate(Organization organization) {
        if (organization == null || organization.getFullName() == null) {
            return null;
        }

        return organizationRepository.findByFullName(organization.getFullName())
                .map(existing -> {
                    updateNonNullFields(existing, organization);
                    return organizationRepository.save(existing);
                })
                .orElseGet(() -> organizationRepository.save(organization));
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
