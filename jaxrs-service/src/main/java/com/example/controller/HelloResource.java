package com.example.controller;

import com.example.model.Hello;
import jakarta.ejb.Stateless;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.ws.rs.*;
import jakarta.ws.rs.core.MediaType;
import jakarta.transaction.Transactional;
import jakarta.ws.rs.core.Response;


@Path("/hello")
@Consumes(MediaType.APPLICATION_JSON)
@Produces(MediaType.APPLICATION_JSON)
@Stateless
public class HelloResource {

    @PersistenceContext(unitName = "MyPU")
    private EntityManager em;

    @POST
    @Transactional
    public Response create(String name) {
        em.persist(new Hello(name));
        return Response.status(Response.Status.CREATED).build();
    }

    @GET
    @Path("/{id}")
    public Response get(@PathParam("id") Long id) {
        Hello hello = em.find(Hello.class, id);
        if (hello == null){
            return Response.status(Response.Status.NOT_FOUND).entity("Object with id " + id + " not found").build();
        }

        return Response.ok(hello).build();
    }
}
