package com.example.infectionservice.service.feigh.clients;

import com.example.infectionservice.dto.MonsterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.context.annotation.Bean;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.ResponseStatus;

import java.util.UUID;

@FeignClient(name = "monster-service", url = "http://localhost:8082/")
public interface MonsterServiceFeighClient {

    @PostMapping()
    @ResponseStatus(HttpStatus.OK)
    MonsterDTO findById(@PathVariable UUID id);
}
