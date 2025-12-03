package com.example.model;

import jakarta.json.bind.annotation.JsonbDateFormat;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotBlank;
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
    @NotBlank
    private String name;

    @NotNull
    private Coordinates coordinates;

    @Min(0)
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
