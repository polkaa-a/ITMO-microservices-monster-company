package com.example.authendpointaggregator.controller;

import com.example.authendpointaggregator.dto.DoorDTO;
import com.example.authendpointaggregator.dto.PageDTO;
import com.example.authendpointaggregator.feignclient.ChildServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doors")
public class DoorController {
    private final ChildServiceFeignClient doorClient;

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @PostMapping
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO save(@RequestBody DoorDTO doorDTO) {
        return doorClient.saveDoor(doorDTO);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE') or hasAuthority('SCARE ASSISTANT')")
    @GetMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO findById(@PathVariable UUID id) {
        return doorClient.findDoorById(id);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE') or hasAuthority('SCARE ASSISTANT')")
    @GetMapping
    @ResponseStatus(HttpStatus.OK)
    public ResponseEntity<PageDTO<DoorDTO>> findAll(@RequestParam(defaultValue = "0") int page,
                                                    @RequestParam(defaultValue = "5") int size,
                                                    @RequestParam(required = false) Boolean status) {
        return doorClient.findAllDoors(page, size, status);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO changeDoor(@PathVariable UUID id) {
        return doorClient.changeDoor(id);
    }

    @PreAuthorize("hasAuthority('ADMIN') or hasAuthority('SCARE ASSISTANT')")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDoor(@PathVariable UUID id) {
        doorClient.deleteDoor(id);
    }

}
