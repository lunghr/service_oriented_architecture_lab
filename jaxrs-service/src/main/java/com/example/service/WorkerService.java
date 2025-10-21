package com.example.service;

import com.example.model.*;
import com.example.repo.WorkerRepository;
import jakarta.enterprise.context.ApplicationScoped;
import jakarta.inject.Inject;
import jakarta.persistence.TypedQuery;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;

import java.util.List;

@ApplicationScoped
public class WorkerService {

    @Inject
    private WorkerRepository workerRepository;

    @Transactional
    public Worker create(NewWorker newWorker) {
        try {
            Worker worker = Worker.fromNewWorker(newWorker);
            workerRepository.save(worker);
            return worker;
        }catch (Throwable ex){
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                    .code(500)
                    .message("⚠\uFE0F Could not create worker: " + ex.getMessage()))
                    .build());
        }
    }

    public List<Worker> get(int page, int size){
        if(page < 0 || size <= 0){
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(ErrorResponse.builder()
                    .code(400)
                    .message("⚠\uFE0F Invalid pagination parameters: page must be >= 0 and size must be > 0"))
                    .build());
        }
        return workerRepository.findWithPagination(page, size);
    }
}
