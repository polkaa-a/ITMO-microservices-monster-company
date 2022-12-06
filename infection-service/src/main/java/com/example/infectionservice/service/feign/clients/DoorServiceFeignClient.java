package com.example.infectionservice.service.feign.clients;

import com.example.infectionservice.dto.DoorDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;

import java.util.UUID;

@FeignClient(name = "child-service")
public interface DoorServiceFeignClient {
    @GetMapping("/doors/{id}")
    DoorDTO findById(@PathVariable UUID id);
}
