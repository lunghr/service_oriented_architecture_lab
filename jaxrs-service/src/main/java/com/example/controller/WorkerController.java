package com.example.controller;

import com.example.model.NewWorker;
import com.example.model.Worker;
import com.example.model.WorkerUpdateDTO;
import com.example.service.WorkerService;
import jakarta.ejb.Stateless;
import jakarta.inject.Inject;
import jakarta.validation.Valid;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.ws.rs.core.Response;

@Path("/workers")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class WorkerController {

    @Inject
    private WorkerService workerService;

    @POST
    public Response createWorker(@Valid NewWorker newWorker) {
        Worker worker = workerService.create(newWorker);
        return Response.status(Response.Status.CREATED).entity(worker).build();
    }

    @GET
    public Response getWorkers(@QueryParam("page") int page, @QueryParam("size") int size){
        return Response.ok(workerService.getWorkers(page, size)).build();
    }

    @GET
    @Path("/{id}")
    public Response getWorkerById(@PathParam("id") Long id){
        return Response.ok(workerService.getWorkerById(id)).build();
    }

    @PATCH
    @Path("/{id}")
    public Response updateWorkerById(@PathParam("id") Long id, @Valid WorkerUpdateDTO workerUpdateDTO){
        return Response.ok(workerService.updateWorker(id, workerUpdateDTO)).build();
    }

    @DELETE
    @Path("/{id}")
    public Response deleteWorkerById(@PathParam("id") Long id){
        workerService.deleteWorkerById(id);
        return Response.status(204).build();
    }


}
