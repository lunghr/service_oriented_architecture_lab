package com.example.controller;

import com.example.dto.NewWorkerDTO;
import com.example.dto.WorkerUpdateDTO;
import com.example.dto.WorkerListResponseDTO;
import com.example.model.*;
import com.example.service.WorkerService;
import jakarta.validation.Valid;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/workers")
@CrossOrigin(origins = "*", allowedHeaders = "*")
public class WorkerController {

    private final WorkerService workerService;

    public WorkerController(WorkerService workerService) {
        this.workerService = workerService;
    }

    @PostMapping
    public ResponseEntity<Worker> createWorker(@Valid @RequestBody NewWorkerDTO newWorkerDTO) {
        return workerService.create(newWorkerDTO);
    }

    @GetMapping
    public ResponseEntity<WorkerListResponseDTO> getWorkers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return workerService.getWorkers(page, size);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Worker> getWorkerById(@PathVariable("id") Long id) {
        return workerService.getWorkerById(id);
    }

    @PatchMapping("/{id}")
    public ResponseEntity<Worker> updateWorkerById(@PathVariable("id") Long id, @Valid @RequestBody WorkerUpdateDTO workerUpdateDTO) {
        return workerService.updateWorker(id, workerUpdateDTO);
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteWorkerById(@PathVariable("id") Long id) {
        return workerService.deleteWorkerById(id);
    }

    @DeleteMapping("/by-start-date")
    public ResponseEntity<?> deleteWorkerByStartDate(@RequestParam("startDate") String date) {
        return workerService.deleteWorkerByStartDate(date);
    }

    @PostMapping("/count-by-start-date")
    public ResponseEntity<Integer> countWorkersByStartDate(@RequestParam("startDate") String date) {
        return workerService.countWorkersWithStartDateBefore(date);
    }

    @PostMapping("/search")
    public ResponseEntity<WorkerListResponseDTO> getWorkersByCriteria(@RequestParam("page") int page, @RequestParam("size") int size, @Valid @RequestBody SearchCriteria searchCriteria) {
        return workerService.getWorkersByCriteria(page, size, searchCriteria);
    }

}
