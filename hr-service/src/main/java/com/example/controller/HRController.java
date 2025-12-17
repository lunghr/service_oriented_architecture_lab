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

    private final static String BASE_URL = "https://localhost:8445/worker-service/api/workers/";

    private final WebClient webClient;

    public HRController(WebClient webClient) {
        this.webClient = webClient;
    }

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
        System.out.println("=== SSL DEBUG ===");
        System.out.println("java.home = " + System.getProperty("java.home"));
        System.out.println("java.version = " + System.getProperty("java.version"));
        System.out.println("user.dir = " + System.getProperty("user.dir"));

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
                Response.builder().message(String.valueOf(newSalary)).build()
        );
    }

    @PostMapping("/hi")
    public ResponseEntity<?> sayHi() {
        return ResponseEntity.ok(
                Response.builder().message("Hi from HR service!").build()
        );
    }


}


