package com.example.infectionservice.controller;

import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.repository.ChildRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

@RequiredArgsConstructor
@RestController
@RequestMapping("/infections")
public class InfectionController {
    private final ChildRepository childRepository;

    @GetMapping("/greeting")
    public String greeting() {
        return "hello infection";
    }

    @GetMapping("/test")
    public String test() {
        List<ChildEntity> childEntitiesList = childRepository.getChildren();
        String test = "";
        for (ChildEntity child: childEntitiesList) {
            test+= child.getName() + "\n";
        }
        return test;
    }
}
