package com.example.mapper;

import com.example.dto.WorkerUpdateDTO;
import com.example.exception.BadRequestException;
import com.example.model.Position;
import com.example.model.Status;
import com.example.model.Worker;
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
            throw new BadRequestException("Position not exist");
        }
    }

    default Status mapStatus(String statusStr, Status currentStatus) {
        if (statusStr == null) {
            return currentStatus;
        }
        try {
            return Status.valueOf(statusStr);
        } catch (IllegalArgumentException e) {
            throw new BadRequestException("Status not exist");
        }
    }
}
