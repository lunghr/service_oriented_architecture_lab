package com.example.model;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "hellos_to_users")
@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
public class Hello {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(nullable = false)
    private String name;

    public Hello(String name) {
        this.name = name;
    }
}
