package com.example.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Table(name = "organizations")
@Entity
@NoArgsConstructor
@AllArgsConstructor
@Setter
@Getter
public class Organization {
    @Id
    @Column(name = "full_name", nullable = false)
    @NotNull
    private String fullName;

    @Column(name = "employees_count", nullable = true)
    private Integer employeesCount;

    @Enumerated(EnumType.STRING)
    @Column(name = "organization_type", nullable = true)
    private OrganizationType organizationType;

    @Column(name = "official_address", nullable = true)
    private String officialAddress;
}
