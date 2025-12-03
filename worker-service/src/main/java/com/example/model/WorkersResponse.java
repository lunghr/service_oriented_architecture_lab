package com.example.model;


import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.List;

@Builder
@Getter
@Setter
public class WorkersResponse {
    List<Worker> content;
    int page;
    int size;
    Long totalElements;
    int totalPages;
    boolean last;
    boolean first;
}
