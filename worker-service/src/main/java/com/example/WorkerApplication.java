package com.example;


import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.persistence.autoconfigure.EntityScan;
import org.springframework.data.jpa.repository.config.EnableJpaRepositories;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@SpringBootApplication
@EnableJpaRepositories(basePackages = "com.example.repo")
@EnableTransactionManagement
@EntityScan(basePackages = "com.example.model")
public class WorkerApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(WorkerApplication.class, args);
    }
}