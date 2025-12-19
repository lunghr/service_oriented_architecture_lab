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

    private static final String NAMESPACE_URI = "http://example.com/hr-service";;

    @PayloadRoot(namespace = NAMESPACE_URI, localPart = "FireEmployeeRequest")
    @ResponsePayload
    public FireEmployeeResponse fireEmployee(@RequestPayload FireEmployeeRequest request) {
        Long id = request.getId();
        System.out.println("\uD83D\uDD25 Firing employee: id=" + id);
        FireEmployeeResponse response = new FireEmployeeResponse();
        Response serviceResponse = new Response();
        serviceResponse.setMessage("Worker with ID " + id + " successfully fired!");
        response.setResponse(serviceResponse);
        System.out.println("✅ Response message: " + serviceResponse.getMessage());
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
        return response;
    }
}
