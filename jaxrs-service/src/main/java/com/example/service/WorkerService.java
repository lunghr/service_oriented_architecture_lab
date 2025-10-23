package com.example.service;

import com.example.model.*;
import com.example.repo.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;

import java.time.format.DateTimeParseException;

import jakarta.inject.Inject;
import jakarta.persistence.criteria.CriteriaBuilder;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

@ApplicationScoped
public class WorkerService {

    @Inject
    private WorkerRepository workerRepository;

    @Inject
    private WorkerMapper workerMapper;

    @Transactional
    public Worker create(NewWorker newWorker) {
        try {
            Worker worker = Worker.fromNewWorker(newWorker);
            workerRepository.save(worker);
            return worker;
        } catch (Throwable ex) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(500)
                            .message("⚠️ Could not create worker: " + ex.getMessage()))
                    .build());
        }
    }

    public WorkersResponse getWorkers(int page, int size) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid pagination parameters: page must be >= 0 and size must be > 0")
                            .build())
                    .build());
        }
        List<Worker> workers = workerRepository.findWithPagination(page, size);
        Long totalElements = workerRepository.countByCriteria(new SearchCriteria());
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return WorkersResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .content(workers)
                .first(page == 1)
                .last(page >= totalPages)
                .size(size)
                .page(page)
                .build();

    }

    public Worker getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id);
        if (worker == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND).entity(
                            ErrorResponse.builder()
                                    .code(Response.Status.NOT_FOUND.getStatusCode())
                                    .message("⚠️ Worker with id " + id + " not found.")
                                    .build())
                    .build());
        }
        return worker;
    }

    public Worker updateWorker(Long id, WorkerUpdateDTO workerUpdateDTO) {
        Worker existingWorker = workerRepository.findById(id);
        if (existingWorker == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.builder()
                            .code(Response.Status.NOT_FOUND.getStatusCode())
                            .message("⚠️ Worker with id " + id + " not found.")
                            .build())
                    .build());
        }
        workerMapper.updateWorkerFromDto(workerUpdateDTO, existingWorker);
        workerRepository.save(existingWorker);
        return workerRepository.findById(id);
    }

    public void deleteWorkerById(Long id) {
        workerRepository.delete(id);
    }


    public void deleteWorkerByStartDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<Worker> workersToDelete = workerRepository.findByStartDate(parsedDate);
            if (workersToDelete.isEmpty()) {
                return;
            }
            Worker futureUnemployed = workersToDelete.getFirst();
            workerRepository.delete(futureUnemployed.getId());
        } catch (DateTimeParseException e) {
            throw new BadRequestException(Response.status(400).entity(
                    ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid date format")
                            .build()
            ).build());
        }
    }

    public Worker getWorkerWithMinSalary() {
        Worker worker = workerRepository.findWorkerWithMinSalary();
        if (worker == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND).entity(
                            ErrorResponse.builder()
                                    .code(Response.Status.NOT_FOUND.getStatusCode())
                                    .message("⚠️ No workers found.")
                                    .build())
                    .build());
        }
        return worker;
    }

    public int countWorkersWithStartDateBefore(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return workerRepository.countByStartDateBefore(parsedDate);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(Response.status(400).entity(
                    ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid date format")
                            .build()
            ).build());
        }
    }

    public WorkersResponse getWorkersByCriteria(int page, int size, SearchCriteria searchCriteria) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid pagination parameters: page must be >= 0 and size must be > 0")
                            .build())
                    .build());
        }
        List<Worker> workers = workerRepository.searchByCriteria(searchCriteria, page, size);
        Long totalElements = workerRepository.countByCriteria(searchCriteria);
        int totalPages = (int) Math.ceil((double) totalElements / size);

        return WorkersResponse.builder()
                .totalElements(totalElements)
                .totalPages(totalPages)
                .content(workers)
                .first(page == 1)
                .last(page >= totalPages)
                .size(size)
                .page(page)
                .build();
    }


}
