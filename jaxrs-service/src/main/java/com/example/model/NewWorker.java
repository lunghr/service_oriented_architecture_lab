package com.example.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDate;


@AllArgsConstructor
@NoArgsConstructor
@Setter
@Getter
public class NewWorker {

    @NotNull
    private String name;

    @NotNull
    private Coordinates coordinates;

    private Long salary;

    @NotNull
    @JsonbDateFormat("yyyy-MM-dd")
    private LocalDate startDate;

    private String position;

    @NotNull
    private String status;

    @NotNull
    private Organization organization;
}
