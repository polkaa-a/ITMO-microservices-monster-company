package com.example.infectionservice.service.feign.clients;

import com.example.infectionservice.dto.MonsterDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "monster-service")
public interface MonsterServiceFeignClient {
    @GetMapping("/monsters/{id}")
    MonsterDTO findById(@PathVariable UUID id);
}
