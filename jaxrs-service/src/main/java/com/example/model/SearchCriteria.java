package com.example.model;

import jakarta.json.bind.annotation.JsonbProperty;
import lombok.Getter;
import lombok.Setter;

import java.util.List;


@Getter
@Setter
public class SearchCriteria {
    private List<String> sort;
    private List<String> filter;
}