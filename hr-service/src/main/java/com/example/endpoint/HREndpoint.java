package com.example.endpoint;

import com.example.generated.*;
import com.example.model.NotFoundException;
import com.fasterxml.jackson.databind.JsonNode;
import jakarta.xml.bind.Marshaller;
import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.oxm.jaxb.Jaxb2Marshaller;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.ws.server.endpoint.annotation.Endpoint;
import org.springframework.ws.server.endpoint.annotation.PayloadRoot;
import org.springframework.ws.server.endpoint.annotation.RequestPayload;
import org.springframework.ws.server.endpoint.annotation.ResponsePayload;
import reactor.core.publisher.Mono;

import javax.xml.transform.stream.StreamResult;
import java.io.StringWriter;

@Endpoint
public class HREndpoint {

    @Autowired
    private Jaxb2Marshaller marshaller;


    private void logResponse(Object response) {
        try {
            StringWriter sw = new StringWriter();
            marshaller.marshal(response, new StreamResult(sw));
            System.out.println("📦 SOAP Response XML:" + sw);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }




    private static final String NAMESPACE_URI = "http://example.com/hr-service";
    private final static String BASE_URL = "https://localhost:8445/worker-service/api/workers/";

    private final WebClient webClient;

    private static final Logger logger = LogManager.getLogger(HREndpoint.class);

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
        long currentSalary = request.getCurrentSalary();
        double coeff = request.getCoeff();
        long newSalary = Math.round(currentSalary * coeff);
        IndexSalaryResponse response = new IndexSalaryResponse();
        System.out.println("\uD83C\uDFF3\uFE0F\u200D\uD83C\uDF08Indexing salary: currentSalary=" + currentSalary + ", coeff=" + coeff);
        response.setNewSalary(newSalary);
        System.out.println("⚠\uFE0FResponse new salary:" + response.getNewSalary());
        logResponse(response);
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
