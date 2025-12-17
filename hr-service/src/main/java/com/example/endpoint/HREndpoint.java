package com.example.endpoint;

import com.example.generated.*;
import com.example.model.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import org.springframework.http.MediaType;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

@Endpoint
public class HREndpoint {

    private static final String NAMESPACE_URI = "http://example.com/hr-service";
    private final static String BASE_URL = "https://localhost:8445/worker-service/api/workers/";

    private final WebClient webClient;

    // WebClient внедряется через конструктор благодаря конфигурации выше
    public HREndpoint(WebClient webClient) {
        this.webClient = webClient;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FireEmployeeRequest")
    @ResponsePayload
    public FireEmployeeResponse fireEmployee(@RequestPayload FireEmployeeRequest request) {
        Long id = request.getId(); // Предполагаем, что в XSD поле называется id
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
                .block(); // Блокируем поток, так как SOAP синхронный

        FireEmployeeResponse response = new FireEmployeeResponse();
        Response serviceResponse = new Response();
        serviceResponse.setMessage("Worker with ID " + id + " successfully fired!");
        response.setResponse(serviceResponse);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "IndexSalaryRequest")
    @ResponsePayload
    public IndexSalaryResponse indexSalary(@RequestPayload IndexSalaryRequest request) {
        Long id = request.getId();
        Double coeff = request.getCoeff();

        // Логирование из старого контроллера
        System.out.println("=== SSL DEBUG ===");
        System.out.println("java.home = " + System.getProperty("java.home"));

        // 1. Получаем текущую зарплату
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

        // 2. Вычисляем новую
        long newSalary = Math.round(salary * coeff);
        String jsonPayload = "{\"salary\":" + newSalary + "}";

        // 3. Обновляем
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

        IndexSalaryResponse response = new IndexSalaryResponse();
        Response serviceResponse = new Response();
        serviceResponse.setMessage(String.valueOf(newSalary));
        response.setResponse(serviceResponse);
        return response;
    }

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "SayHiRequest")
    @ResponsePayload
    public SayHiResponse sayHi(@RequestPayload SayHiRequest request) {
        SayHiResponse response = new SayHiResponse();
        Response serviceResponse = new Response();
        serviceResponse.setMessage("Hi from HR service!");
        response.setResponse(serviceResponse);
        return response;
    }
}
