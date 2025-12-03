package com.example.config;

import com.example.model.ErrorResponse;
import jakarta.json.bind.JsonbException;
import jakarta.ws.rs.ProcessingException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ProcessingExceptionMapper implements ExceptionMapper<ProcessingException> {

    private static final Logger logger = LoggerFactory.getLogger(ProcessingExceptionMapper.class);

    @Override
    public Response toResponse(ProcessingException exception) {
        String message = exception.getMessage();

        if (exception.getCause() instanceof JsonbException) {
            message = exception.getCause().getMessage();
        }

        ErrorResponse er = ErrorResponse.builder()
                .code(Response.Status.BAD_REQUEST.getStatusCode())
                .message("⚠️ Invalid data: " + message)
                .build();
        logger.warn(er.getMessage());

        return Response.status(er.getCode())
                .entity(er)
                .build();
    }
}