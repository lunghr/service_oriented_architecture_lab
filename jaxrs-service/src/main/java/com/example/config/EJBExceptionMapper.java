package com.example.config;

import com.example.model.ErrorResponse;
import jakarta.ejb.EJBException;
import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.WebApplicationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class EJBExceptionMapper implements ExceptionMapper<EJBException> {

    private static final Logger logger = LoggerFactory.getLogger(EJBExceptionMapper.class);

    @Override
    public Response toResponse(EJBException exception) {
        if (exception.getCause() instanceof BadRequestException bre) {
            return bre.getResponse();
        }

        return Response.status(Response.Status.INTERNAL_SERVER_ERROR)
                .entity(ErrorResponse.builder()
                        .message(exception.getMessage())
                        .code(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode())
                        .build())
                .build();
    }

}
