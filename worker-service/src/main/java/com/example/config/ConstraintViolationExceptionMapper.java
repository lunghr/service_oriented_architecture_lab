package com.example.config;

import com.example.model.ErrorResponse;
import jakarta.validation.ConstraintViolationException;
import jakarta.ws.rs.core.Response;
import jakarta.ws.rs.ext.ExceptionMapper;
import jakarta.ws.rs.ext.Provider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

@Provider
public class ConstraintViolationExceptionMapper implements ExceptionMapper<ConstraintViolationException> {
    private static final Logger logger = LoggerFactory.getLogger(ConstraintViolationExceptionMapper.class);

    @Override
    public Response toResponse(ConstraintViolationException exception) {
        ErrorResponse er = ErrorResponse.builder()
                .code(Response.Status.BAD_REQUEST.getStatusCode())
                .message(exception.getConstraintViolations()
                        .stream()
                        .map(cv -> "⚠️ Invalid data: " + cv.getPropertyPath() + " " + cv.getMessage())
                        .reduce((msg1, msg2) -> msg1 + ", " + msg2)
                        .orElse("Validation error"))
                .build();
        logger.warn(er.getMessage());

        return Response.status(er.getCode())
                .entity(er)
                .build();
    }
}