package com.example.service;

import com.example.model.*;
import com.example.repo.WorkerRepository;

import java.time.format.DateTimeParseException;

import jakarta.transaction.Transactional;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.NotFoundException;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.data.jpa.domain.Specification;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

@Service
public class WorkerService {

    private final WorkerRepository workerRepository;
    private final OrganizationService organizationService;
    private final WorkerMapper workerMapper;

    public WorkerService(WorkerRepository workerRepository,
                         OrganizationService organizationService,
                         WorkerMapper workerMapper) {
        this.workerRepository = workerRepository;
        this.organizationService = organizationService;
        this.workerMapper = workerMapper;
    }

    @Transactional
    public ResponseEntity<Worker> create(NewWorker newWorker) {
        try {
            Worker worker = Worker.fromNewWorker(newWorker);

            if (worker.getOrganization() != null) {
                Organization savedOrg = organizationService.saveOrUpdate(worker.getOrganization());
                worker.setOrganization(savedOrg);
            }

            return ResponseEntity.status(HttpStatus.CREATED).body(workerRepository.save(worker));
        } catch (Throwable ex) {
            throw new BadRequestException(ex.getMessage());
        }
    }

    @Transactional
    public ResponseEntity<WorkersResponse> getWorkers(int page, int size) {
        System.out.println("Fetching workers with page: " + page + " and size: " + size);
        if (page < 1 || size <= 0) {
            throw new BadRequestException("Invalid pagination parameters: page must be >= 1 and size must be > 0");
        }
        Page<Worker> pageResult = workerRepository.findAll(PageRequest.of(page, size)); //TODO check if page-1 needed
        return ResponseEntity.ok().body(WorkersResponse.builder()
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .content(pageResult.getContent())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .size(size)
                .page(page)
                .build());
    }

    @Transactional
    public ResponseEntity<Worker> getWorkerById(Long id) {
        Worker worker = workerRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Worker with id " + id + " not found."));
        return ResponseEntity.ok().body(worker);
    }

    @Transactional
    public ResponseEntity<Worker> updateWorker(Long id, WorkerUpdateDTO workerUpdateDTO) {
        Worker existingWorker = workerRepository.findById(id).orElseThrow(() ->
                new NotFoundException("Worker with id " + id + " not found.")
        );

        workerMapper.updateWorkerFromDto(workerUpdateDTO, existingWorker);
        if (existingWorker.getOrganization() != null) {
            Organization savedOrg = organizationService.saveOrUpdate(existingWorker.getOrganization());
            existingWorker.setOrganization(savedOrg);
        }

        return ResponseEntity.ok().body(workerRepository.save(existingWorker));
    }

    @Transactional
    public ResponseEntity<?> deleteWorkerById(Long id) {
        workerRepository.deleteById(id);
        return ResponseEntity.noContent().build();
    }

    @Transactional
    public ResponseEntity<?> deleteWorkerByStartDate(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            workerRepository.deleteByStartDate(parsedDate);
            return ResponseEntity.noContent().build();
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format");
        }
    }

    @Transactional
    public ResponseEntity<Integer> countWorkersWithStartDateBefore(String date) {
        try {
            LocalDate parsedDate = LocalDate.parse(date);
            return ResponseEntity.ok().body(workerRepository.countByStartDateBefore(parsedDate));
        } catch (DateTimeParseException e) {
            throw new BadRequestException("Invalid date format");
        }
    }

    @Transactional
    public ResponseEntity<WorkersResponse> getWorkersByCriteria(int page, int size, SearchCriteria searchCriteria) {
        if (page < 1 || size <= 0) {
            throw new BadRequestException("Invalid pagination parameters");
        }

        Sort sort = Sort.unsorted();
        if (searchCriteria.getSort() != null && !searchCriteria.getSort().isEmpty()) {
            List<Sort.Order> orders = new ArrayList<>();
            for (String s : searchCriteria.getSort()) {
                try {
                    if (s.startsWith("-")) {
                        orders.add(Sort.Order.desc(s.substring(1)));
                    } else {
                        orders.add(Sort.Order.asc(s));
                    }
                } catch (IllegalArgumentException ignored) {
                }
            }
            sort = Sort.by(orders);
        }

        Pageable pageable = PageRequest.of(page - 1, size, sort);

        Page<Worker> pageResult;
        if (searchCriteria.getFilter() != null && !searchCriteria.getFilter().isEmpty()) {
            Specification<Worker> spec = new WorkerSpecification(searchCriteria.getFilter());
            pageResult = workerRepository.findAll(spec, pageable);
        } else {
            pageResult = workerRepository.findAll(pageable);
        }

        return ResponseEntity.ok().body(WorkersResponse.builder()
                .totalElements(pageResult.getTotalElements())
                .totalPages(pageResult.getTotalPages())
                .content(pageResult.getContent())
                .first(pageResult.isFirst())
                .last(pageResult.isLast())
                .size(size)
                .page(page)
                .build());
    }
}