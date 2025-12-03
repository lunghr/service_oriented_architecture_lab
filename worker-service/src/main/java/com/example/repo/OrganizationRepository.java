package com.example.repo;

import com.example.model.Organization;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface OrganizationRepository extends JpaRepository<@NonNull Organization, @NonNull Long> {
    Optional<Organization> findByFullName(String fullName);
}
