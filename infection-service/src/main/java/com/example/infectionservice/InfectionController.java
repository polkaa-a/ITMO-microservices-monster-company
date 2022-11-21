package com.example.infectionservice;

import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RequiredArgsConstructor
@RestController
@RequestMapping("/infections")
public class InfectionController {

    @GetMapping("/greeting")
    public String greeting() {
        return "hello infection";
    }
}
