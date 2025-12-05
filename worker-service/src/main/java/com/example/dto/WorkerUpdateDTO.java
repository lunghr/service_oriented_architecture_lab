package com.example.dto;

import com.example.model.Coordinates;
import com.example.model.Organization;
import jakarta.validation.constraints.Min;
import lombok.*;

import java.time.LocalDate;

@AllArgsConstructor
@NoArgsConstructor
@Builder
@Getter
@Setter
public class WorkerUpdateDTO {
    private String name;
    private Coordinates coordinates;
    @Min(0)
    private Long salary;
    private LocalDate startDate;
    private String position;
    private String status;
    private Organization organization;
}
