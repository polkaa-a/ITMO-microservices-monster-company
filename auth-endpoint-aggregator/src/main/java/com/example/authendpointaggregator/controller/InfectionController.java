package com.example.authendpointaggregator.controller;

import com.example.authendpointaggregator.dto.InfectionRequestDTO;
import com.example.authendpointaggregator.dto.InfectionResponseDTO;
import com.example.authendpointaggregator.dto.PageDTO;
import com.example.authendpointaggregator.feignclient.InfectionServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@PreAuthorize("hasAuthority('ADMIN') or hasAuthority('DISINFECTOR')")
@RestController
@RequestMapping("/infections")
public class InfectionController {
    private final InfectionServiceFeignClient infectionClient;

    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public InfectionResponseDTO save(@RequestBody InfectionRequestDTO infectionRequestDTO) {
        return infectionClient.saveInfection(infectionRequestDTO);
    }

    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionResponseDTO findById(@PathVariable UUID id) {
        return infectionClient.findInfectionById(id);
    }


    @GetMapping
    public ResponseEntity<PageDTO<InfectionResponseDTO>> findAll(@RequestParam(defaultValue = "0") int page,
                                                                 @RequestParam(defaultValue = "5") int size,
                                                                 @RequestParam(required = false) UUID monsterId,
                                                                 @RequestParam(required = false) Date date) {
        return infectionClient.findAllInfections(page, size, monsterId, date);
    }

    @PatchMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public InfectionResponseDTO updateCureDate(@RequestBody Map<String, Date> cureDate, @PathVariable UUID id) {
        return infectionClient.updateCureDate(cureDate, id);
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void delete(@PathVariable UUID id) {
        infectionClient.deleteInfection(id);
    }
}
