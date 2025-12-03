package com.example.service;

import com.example.model.*;
import com.example.repo.WorkerRepositoryJAX;

import java.time.format.DateTimeParseException;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import jakarta.ws.rs.core.Response;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.List;

@Service
public class WorkerService {
    private final WorkerRepositoryJAX workerRepositoryJAX;

    private final WorkerMapper workerMapper;

    public WorkerService(WorkerRepositoryJAX workerRepositoryJAX, WorkerMapper workerMapper) {
        this.workerRepositoryJAX = workerRepositoryJAX;
        this.workerMapper = workerMapper;
    }

    @Transactional
    public Worker create(NewWorker newWorker) {
        try {
            Worker worker = Worker.fromNewWorker(newWorker);
            worker = workerRepositoryJAX.save(worker);
            return worker;
        } catch (Throwable ex) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(500)
                            .message("⚠️ Could not create worker: " + ex.getMessage()))
                    .build());
        }
    }

    @Transactional
    public WorkersResponse getWorkers(int page, int size) {
        System.out.println("Fetching workers with page: " + page + " and size: " + size);
        if (page < 0 || size <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid pagination parameters: page must be >= 0 and size must be > 0")
                            .build())
                    .build());
        }
        List<Worker> workers = workerRepositoryJAX.findWithPagination(page, size);
        Long totalElements = workerRepositoryJAX.countByCriteria(new SearchCriteria());
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

    @Transactional
    public Worker getWorkerById(Long id) {
        Worker worker = workerRepositoryJAX.findById(id);
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

    @Transactional
    public Worker updateWorker(Long id, WorkerUpdateDTO workerUpdateDTO) {
        Worker existingWorker = workerRepositoryJAX.findById(id);
        if (existingWorker == null) {
            throw new NotFoundException(Response.status(Response.Status.NOT_FOUND)
                    .entity(ErrorResponse.builder()
                            .code(Response.Status.NOT_FOUND.getStatusCode())
                            .message("⚠️ Worker with id " + id + " not found.")
                            .build())
                    .build());
        }
        workerMapper.updateWorkerFromDto(workerUpdateDTO, existingWorker);
        workerRepositoryJAX.save(existingWorker);
        return workerRepositoryJAX.findById(id);
    }

    @Transactional
    public void deleteWorkerById(Long id) {
        workerRepositoryJAX.delete(id);
    }

    @Transactional
    public void deleteWorkerByStartDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            List<Worker> workersToDelete = workerRepositoryJAX.findByStartDate(parsedDate);
            if (workersToDelete.isEmpty()) {
                return;
            }
            Worker futureUnemployed = workersToDelete.getFirst();
            workerRepositoryJAX.delete(futureUnemployed.getId());
        } catch (DateTimeParseException e) {
            throw new BadRequestException(Response.status(400).entity(
                    ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid date format")
                            .build()
            ).build());
        }
    }


    @Transactional
    public int countWorkersWithStartDateBefore(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return workerRepositoryJAX.countByStartDateBefore(parsedDate);
        } catch (DateTimeParseException e) {
            throw new BadRequestException(Response.status(400).entity(
                    ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid date format")
                            .build()
            ).build());
        }
    }

    @Transactional
    public WorkersResponse getWorkersByCriteria(int page, int size, SearchCriteria searchCriteria) {
        if (page < 0 || size <= 0) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                            .code(400)
                            .message("⚠️ Invalid pagination parameters: page must be >= 0 and size must be > 0")
                            .build())
                    .build());
        }
        List<Worker> workers = workerRepositoryJAX.searchByCriteria(searchCriteria, page, size);
        Long totalElements = workerRepositoryJAX.countByCriteria(searchCriteria);
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
