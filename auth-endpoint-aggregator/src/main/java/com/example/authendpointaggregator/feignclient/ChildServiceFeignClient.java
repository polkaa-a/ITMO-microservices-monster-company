package com.example.authendpointaggregator.feignclient;

import com.example.authendpointaggregator.dto.ChildRequestDTO;
import com.example.authendpointaggregator.dto.ChildResponseDTO;
import com.example.authendpointaggregator.dto.DoorDTO;
import com.example.authendpointaggregator.dto.PageDTO;
import org.springframework.cloud.openfeign.FeignClient;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.UUID;

@FeignClient(name = "child-service")
public interface ChildServiceFeignClient {

    @PostMapping("/child")
    ChildResponseDTO addChild(@RequestBody ChildRequestDTO childDTO);

    @GetMapping("/child/{id}")
    ChildResponseDTO findChildById(@PathVariable UUID id);

    @GetMapping("/child")
    ResponseEntity<PageDTO<ChildResponseDTO>> getChildren(@RequestParam(defaultValue = "0") int page,
                                                          @RequestParam(defaultValue = "5") int size);

    @DeleteMapping("/child/{id}")
    void deleteChild(@PathVariable UUID id);

    @PostMapping("/doors")
    DoorDTO saveDoor(@RequestBody DoorDTO doorDTO);

    @GetMapping("/doors/{id}")
    DoorDTO findDoorById(@PathVariable UUID id);

    @GetMapping("/doors")
    ResponseEntity<PageDTO<DoorDTO>> findAllDoors(@RequestParam(defaultValue = "0") int page,
                                                  @RequestParam(defaultValue = "5") int size,
                                                  @RequestParam(required = false) Boolean status);

    @PutMapping("/doors/{id}")
    DoorDTO changeDoor(@PathVariable UUID id);

    @DeleteMapping("/doors/{id}")
    void deleteDoor(@PathVariable UUID id);
}
