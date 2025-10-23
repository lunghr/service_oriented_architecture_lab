//package com.example.service;
//
//import com.example.model.ErrorResponse;
//import jakarta.json.bind.JsonbException;
//import jakarta.ws.rs.BadRequestException;
//import jakarta.ws.rs.core.Response;
//import jakarta.ws.rs.ext.ExceptionMapper;
//import jakarta.ws.rs.ext.Provider;
//import jakarta.validation.ConstraintViolationException;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.util.HashMap;
//import java.util.Map;
//import java.util.stream.Collectors;
//
//@Provider
//public class GlobalExceptionMapper implements ExceptionMapper<Exception> {
//
//    private static final Logger LOGGER = LoggerFactory.getLogger(GlobalExceptionMapper.class);
//
//    private static final Map<Class<? extends Throwable>, ExceptionHandler> HANDLERS = new HashMap<>();
//
//    static {
////        HANDLERS.put(ConstraintViolationException.class, (exception) -> {
////            ConstraintViolationException cve = (ConstraintViolationException) exception;
////            String message = cve.getConstraintViolations()
////                    .stream()
////                    .map(cv -> cv.getPropertyPath() + ": " + cv.getMessage())
////                    .collect(Collectors.joining(", "));
////            LOGGER.warn("\uD83D\uDEAB Validation error: {}", message);
////            return new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), message);
////        });
//        HANDLERS.put(BadRequestException.class, (exception) -> {
//            BadRequestException bre = (BadRequestException) exception;
//            LOGGER.warn("\uD83D\uDEAB Bad request: {}", bre.getMessage());
//            return new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), bre.getMessage());
//        });
//        HANDLERS.put(JsonbException.class, (exception) -> {
//            JsonbException jbe = (JsonbException) exception;
//            LOGGER.warn("\uD83D\uDEAB Invalid enum field: {}", jbe.getMessage());
//            return new ErrorResponse(Response.Status.BAD_REQUEST.getStatusCode(), jbe.getMessage());
//        });
//    }
//
//    private static final ExceptionHandler DEFAULT_HANDLER = (exception) -> {
//        LOGGER.error("Unexpected error occurred", exception);
//        return new ErrorResponse(Response.Status.INTERNAL_SERVER_ERROR.getStatusCode(), "Internal server error");
//    };
//
//    @Override
//    public Response toResponse(Exception exception) {
//        ExceptionHandler handler = HANDLERS.entrySet().stream()
//                .filter(entry -> entry.getKey().isInstance(exception))
//                .map(Map.Entry::getValue)
//                .findFirst()
//                .orElse(DEFAULT_HANDLER);
//        ErrorResponse response = handler.handle(exception);
//        return Response.status(response.getCode())
//                .entity(response)
//                .build();
//    }
//
//    @FunctionalInterface
//    private interface ExceptionHandler {
//        ErrorResponse handle(Exception exception);
//    }
//}
//
//
