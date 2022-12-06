package com.example.authendpointaggregator.feignclient;

import com.example.authendpointaggregator.dto.*;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

@FeignClient(name = "infection-service")
public interface InfectionServiceFeignClient {
    @PostMapping("/infections")
    InfectionResponseDTO saveInfection(@RequestBody InfectionRequestDTO infectionRequestDTO);

    @GetMapping("/infections/{id}")
    InfectionResponseDTO findInfectionById(@PathVariable UUID id);

    @GetMapping("/infections")
    ResponseEntity<PageDTO<InfectionResponseDTO>> findAllInfections(@RequestParam(defaultValue = "0") int page,
                                                                    @RequestParam(defaultValue = "5") int size,
                                                                    @RequestParam(required = false) UUID monsterId,
                                                                    @RequestParam(required = false) Date date);

    @PatchMapping("/infections/{id}")
    InfectionResponseDTO updateCureDate(@RequestBody Map<String, Date> cureDate, @PathVariable UUID id);

    @DeleteMapping("/infections/{id}")
    void deleteInfection(@PathVariable UUID id);

    @PostMapping("/infected-things")
    InfectedThingResponseDTO saveInfectedThing(@RequestBody InfectedThingRequestDTO infectionDTO);

    @GetMapping("/infected-things/{id}")
    InfectedThingResponseDTO findInfectedThingById(@PathVariable UUID id);

    @GetMapping("/infected-things")
    ResponseEntity<PageDTO<InfectedThingResponseDTO>> findAllInfectedThings(@RequestParam(defaultValue = "0") int page,
                                                                            @RequestParam(defaultValue = "5") int size,
                                                                            @RequestParam(required = false) UUID doorId);

    @DeleteMapping("/infected-things/{id}")
    void deleteInfectedThing(@PathVariable UUID id);
}
