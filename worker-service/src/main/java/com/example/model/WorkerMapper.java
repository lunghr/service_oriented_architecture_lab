package com.example.model;

import jakarta.ws.rs.BadRequestException;
import jakarta.ws.rs.core.Response;
import org.mapstruct.Mapper;
import org.mapstruct.Mapping;
import org.mapstruct.MappingTarget;
import org.mapstruct.NullValuePropertyMappingStrategy;

@Mapper(componentModel = "spring", nullValuePropertyMappingStrategy = NullValuePropertyMappingStrategy.IGNORE)
public interface WorkerMapper {


    @Mapping(target = "id", ignore = true)
    @Mapping(target = "creationDate", ignore = true)
    @Mapping(target = "position", expression = "java(mapPosition(updateDTO.getPosition(), worker.getPosition()))")
    @Mapping(target = "status", expression = "java(mapStatus(updateDTO.getStatus(), worker.getStatus()))")
    void updateWorkerFromDto(WorkerUpdateDTO updateDTO, @MappingTarget Worker worker);

    default Position mapPosition(String positionStr, Position currentPosition) {
        if (positionStr == null) {
            return currentPosition;
        }
        try {
            return Position.valueOf(positionStr);
        } catch (IllegalArgumentException e) {
            throw new jakarta.ws.rs.BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(
                    ErrorResponse.builder()
                            .code(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("⚠️ Position not exist")
                            .build()
            ).build());
        }
    }

    default Status mapStatus(String statusStr, Status currentStatus) {
        if (statusStr == null) {
            return currentStatus;
        }
        try {
            return Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException(Response.status(Response.Status.BAD_REQUEST).entity(
                    ErrorResponse.builder()
                            .code(Response.Status.BAD_REQUEST.getStatusCode())
                            .message("⚠️ Status not exist")
                            .build()
            ).build());
        }
    }
}
