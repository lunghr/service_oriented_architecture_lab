package com.example.controller;

import com.example.model.NotFoundException;
import com.example.model.Response;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.reactive.function.client.WebClient;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/hr")
public class HRController {

    private final static String BASE_URL = "http://localhost:8080/jaxrs-service/api/workers/";

    private final WebClient webClient = WebClient.create();

    @PostMapping(value = "/fire/{id}", produces = "application/json")
    public ResponseEntity<?> fireEmployee(@PathVariable("id") Long id) {
        String jsonPayload = "{\"status\":\"FIRED\"}";
        webClient.patch()
                .uri(BASE_URL + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonPayload)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else if (response.statusCode().is4xxClientError()) {
                        return Mono.error(new NotFoundException("Worker with ID " + id + " not found."));
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .block();
        return ResponseEntity.ok(
                Response.builder().message("Worker with ID " + id + " successfully fired!").build()
        );
    }

    @PostMapping(value = "/index/{id}/{coeff}")
    public ResponseEntity<?> indexSalary(@PathVariable("id") Long id, @PathVariable("coeff") Double coeff) {

        Long salary = webClient.get()
                .uri(BASE_URL + id)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(JsonNode.class)
                                .map(jsonNode -> jsonNode.get("salary").asLong());
                    } else if (response.statusCode().is4xxClientError()) {
                        return Mono.error(new NotFoundException("Worker with ID " + id + " not found."));
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .block();

        long newSalary = Math.round(salary * coeff);
        String jsonPayload = "{\"salary\":" + newSalary + "}";
        webClient.patch()
                .uri(BASE_URL + id)
                .contentType(MediaType.APPLICATION_JSON)
                .bodyValue(jsonPayload)
                .exchangeToMono(response -> {
                    if (response.statusCode().is2xxSuccessful()) {
                        return response.bodyToMono(String.class);
                    } else if (response.statusCode().is4xxClientError()) {
                        return Mono.error(new NotFoundException("Worker with ID " + id + " not found."));
                    } else {
                        return response.createException().flatMap(Mono::error);
                    }
                })
                .block();

        return ResponseEntity.ok(
                Response.builder().message("Worker with ID " + id + " salary successfully indexed: " + newSalary ).build()
        );
    }


}


