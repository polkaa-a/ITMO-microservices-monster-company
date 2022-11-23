package com.example.infectionservice.controller;

import com.example.infectionservice.dto.DoorDTO;
import com.example.infectionservice.mapper.DoorMapper;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.service.DoorService;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@RestController
@RequestMapping("/doors")
public class DoorController {
    private final DoorService doorService;
    private final DoorMapper doorMapper;

    @GetMapping("/active")
    @ResponseStatus(HttpStatus.OK)
    public List<DoorDTO> getActiveDoors() {
        List<DoorEntity> doorEntities = doorService.findAllActiveDoors();
        return doorEntities
                .stream()
                .map((doorMapper::mapEntityToDto))
                .toList();
    }

    @PutMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public DoorDTO changeDoor(@PathVariable UUID id) {
        return doorMapper.mapEntityToDto(doorService.changeActive(id));
    }

    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.OK)
    public void deleteDoor(@PathVariable UUID id) {
        doorService.deleteDoor(id);
    }
}
