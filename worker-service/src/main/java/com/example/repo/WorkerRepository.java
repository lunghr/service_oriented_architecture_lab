package com.example.repo;

import com.example.model.Worker;
import org.jspecify.annotations.NonNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import java.time.LocalDate;

public interface WorkerRepository extends JpaRepository<@NonNull Worker, Long>, JpaSpecificationExecutor<Worker> {
    void deleteByStartDate(LocalDate startDate);
    int countByStartDateBefore(LocalDate startDateBefore);
}
