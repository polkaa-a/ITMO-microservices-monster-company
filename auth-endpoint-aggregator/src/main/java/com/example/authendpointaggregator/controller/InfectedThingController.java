package com.example.authendpointaggregator.controller;

import com.example.authendpointaggregator.dto.InfectedThingRequestDTO;
import com.example.authendpointaggregator.dto.InfectedThingResponseDTO;
import com.example.authendpointaggregator.dto.PageDTO;
import com.example.authendpointaggregator.feignclient.InfectionServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DISINFECTOR')")
@RestController
@RequestMapping("/infected-things")
public class InfectedThingController {
    private final InfectionServiceFeignClient infectedThingClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfectedThingResponseDTO save(@RequestBody InfectedThingRequestDTO infectionDTO) {
        return infectedThingClient.saveInfectedThing(infectionDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectedThingResponseDTO findById(@PathVariable UUID id) {
        return infectedThingClient.findInfectedThingById(id);
    }


    @GetMapping
    public ResponseEntity<PageDTO<InfectedThingResponseDTO>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                     @RequestParam(defaultValue = "5") int size,
                                                                     @RequestParam(required = false) UUID doorId) {
        return infectedThingClient.findAllInfectedThings(page, size, doorId);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectedThingClient.deleteInfectedThing(id);
    }
}
