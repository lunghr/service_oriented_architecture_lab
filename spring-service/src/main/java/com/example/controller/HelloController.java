package com.example.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

import java.util.Map;

@Controller
public class HelloController {

    @GetMapping("/hello")
    @ResponseBody
    public Map<String, String> sayHello() {
        return Map.of("message", "Hello from Spring service!");
    }
}
