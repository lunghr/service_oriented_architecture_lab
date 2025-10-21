package com.example.controller;

import com.example.model.NewWorker;
import com.example.model.Worker;
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
        return Response.ok(workerService.get(page, size)).build();
    }


}
