package com.example.model;

import com.example.dto.NewWorkerDTO;
import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@Table(name = "workers")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Worker {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(name = "name", nullable = false)
    private String name;

    @Embedded
    @AttributeOverrides({
            @AttributeOverride(name = "x", column = @Column(name = "x", nullable = false)),
            @AttributeOverride(name = "y", column = @Column(name = "y", nullable = false))
    })
    private Coordinates coordinates;

    @Column(name = "salary")
    private Long salary;

    @Column(name = "start_date", nullable = false)
    private LocalDate startDate;

    @Column(name = "creation_date", nullable = false)
    @Builder.Default
    private ZonedDateTime creationDate = ZonedDateTime.now();

    @Enumerated(EnumType.STRING)
    @Column(name = "position")
    private Position position;

    @Enumerated(EnumType.STRING)
    @Column(name = "status", nullable = false)
    private Status status;

    @ManyToOne
    @JoinColumn(name = "organization", nullable = false)
    private Organization organization;

    public static Worker fromNewWorker(NewWorkerDTO newWorkerDTO){
        return Worker.builder()
                .name(newWorkerDTO.getName())
                .coordinates(newWorkerDTO.getCoordinates())
                .salary(newWorkerDTO.getSalary() != null ? newWorkerDTO.getSalary() : 0)
                .startDate(newWorkerDTO.getStartDate())
                .position(newWorkerDTO.getPosition() != null ? Position.valueOf(newWorkerDTO.getPosition()) : null)
                .status(Status.valueOf(newWorkerDTO.getStatus()))
                .organization(newWorkerDTO.getOrganization())
                .build();
    }
}


