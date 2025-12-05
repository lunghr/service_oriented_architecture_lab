package com.example.dto;


import com.example.model.Worker;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class WorkerListResponseDTO {
    List<Worker> content;
    int page;
    int size;
    Long totalElements;
    int totalPages;
    boolean last;
    boolean first;
}
