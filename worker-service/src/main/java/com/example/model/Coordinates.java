package com.example.model;

import jakarta.persistence.*;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Embeddable
@NoArgsConstructor
@AllArgsConstructor
@Getter
@Setter
public class Coordinates {
    @Column(name = "x", nullable = false)
    private Double x;

    @Column(name ="y", nullable = false)
    private Double y;
}
