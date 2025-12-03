package com.example.controller;

import com.example.model.NewWorker;
import com.example.model.SearchCriteria;
import com.example.model.Worker;
import com.example.model.WorkerUpdateDTO;
import com.example.service.WorkerService;
import jakarta.inject.Inject;

import jakarta.validation.Valid;
import jakarta.ws.rs.core.Response;
import org.springframework.web.bind.annotation.*;

import java.util.Map;

@RestController
@RequestMapping("/workers")
public class WorkerController {

    @Inject
    private WorkerService workerService;

    @PostMapping
    public Response createWorker(@Valid @RequestBody NewWorker newWorker) {
        Worker worker = workerService.create(newWorker);
        return Response.status(Response.Status.CREATED).entity(worker).build();
    }

    @GetMapping
    public Response getWorkers(@RequestParam("page") int page, @RequestParam("size") int size) {
        return Response.ok(workerService.getWorkers(page, size)).build();
    }

    @GetMapping("/{id}")
    public Response getWorkerById(@PathVariable("id") Long id) {
        return Response.ok(workerService.getWorkerById(id)).build();
    }

    @PatchMapping("/{id}")
    public Response updateWorkerById(@PathVariable("id") Long id,@Valid @RequestBody WorkerUpdateDTO workerUpdateDTO) {
        return Response.ok(workerService.updateWorker(id, workerUpdateDTO)).build();
    }

    @DeleteMapping("/{id}")
    public Response deleteWorkerById(@PathVariable("id") Long id) {
        workerService.deleteWorkerById(id);
        return Response.noContent().build();
    }

    @DeleteMapping("/by-start-date")
    public Response deleteWorkerByStartDate(@RequestParam("startDate") String date) {
        workerService.deleteWorkerByStartDate(date);
        return Response.noContent().build();
    }

    @PostMapping("/count-by-start-date")
    public Response countWorkersByStartDate(@RequestParam("startDate") String date) {
        int count = workerService.countWorkersWithStartDateBefore(date);
        return Response.ok(Map.of("count", count)).build();
    }

    @PostMapping("/search")
    public Response getWorkersByCriteria(@RequestParam("page") int page, @RequestParam("size") int size,@Valid @RequestBody SearchCriteria searchCriteria){
        return Response.ok(workerService.getWorkersByCriteria(page, size, searchCriteria)).build();
    }

}
