package com.example;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class HRApplication {
    public static void main(String[] args) {
        org.springframework.boot.SpringApplication.run(HRApplication.class, args);
    }
}
