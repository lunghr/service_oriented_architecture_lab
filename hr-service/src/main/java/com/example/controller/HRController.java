package com.example.controller;

import com.example.exception.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.*;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.RestTemplate;

@RestController
@RequestMapping("/hr")
public class HRController {

    private final static String BASE_URL = "https://localhost:8445/worker-service/api/workers/";
    private final RestTemplate restTemplate;

    public HRController(RestTemplate restTemplate) {
        this.restTemplate = restTemplate;
    }

    @PostMapping(value = "/fire/{id}", produces = "application/json")
    public ResponseEntity<?> fireEmployee(@PathVariable("id") Long id) {
        String jsonPayload = "{\"status\":\"FIRED\"}";
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);
        HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);
        try {
            restTemplate.exchange(BASE_URL + id, HttpMethod.PATCH, request, String.class);
            return ResponseEntity.ok().body("Worker with ID " + id + " successfully fired!");
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Worker with ID " + id + " not found.");
        }
    }

    @PostMapping(value = "/index/{id}/{coeff}")
    public ResponseEntity<?> indexSalary(@PathVariable("id") Long id, @PathVariable("coeff") Double coeff) {
        try {
            ResponseEntity<JsonNode> response = restTemplate.getForEntity(BASE_URL + id, JsonNode.class);
            Long salary = response.getBody().get("salary").asLong();

            long newSalary = Math.round(salary * coeff);
            String jsonPayload = "{\"salary\":" + newSalary + "}";
            HttpHeaders headers = new HttpHeaders();
            headers.setContentType(MediaType.APPLICATION_JSON);
            HttpEntity<String> request = new HttpEntity<>(jsonPayload, headers);

            restTemplate.exchange(BASE_URL + id, HttpMethod.PATCH, request, String.class);
            return ResponseEntity.ok().body(String.valueOf(newSalary));
        } catch (HttpClientErrorException.NotFound e) {
            throw new NotFoundException("Worker with ID " + id + " not found.");
        }
    }

    @PostMapping("/hello")
    public ResponseEntity<String> hello() {
        return ResponseEntity.ok("Hello from HR Service!");
    }
}
