package com.example.model;

import jakarta.persistence.*;
import lombok.*;

import java.time.LocalDate;
import java.time.ZonedDateTime;

@Entity
@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
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

    public static Worker fromNewWorker(NewWorker newWorker){
        return Worker.builder()
                .name(newWorker.getName())
                .coordinates(newWorker.getCoordinates())
                .salary(newWorker.getSalary() != null ? newWorker.getSalary() : null)
                .startDate(newWorker.getStartDate())
                .position(newWorker.getPosition() != null ? Position.valueOf(newWorker.getPosition()) : null)
                .status(Status.valueOf(newWorker.getStatus()))
                .organization(newWorker.getOrganization())
                .build();
    }
}


