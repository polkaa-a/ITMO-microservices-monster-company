package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.repository.DoorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DoorService {

    private final DoorRepository doorRepository;

    private static final String EXC_MES_ID = "none door was found by id";

    public DoorEntity findById(UUID doorId) {
        return doorRepository.findById(doorId).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + ": " + doorId)
        );
    }

    public DoorEntity save(DoorEntity doorEntity) {
        return doorRepository.save(doorEntity);
    }

    public List<DoorEntity> findAllActiveDoors() {
        return doorRepository.findAllByActive();
    }

    public DoorEntity changeActive(UUID id) {
        DoorEntity doorEntity = doorRepository.findById(id).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + " " + id)
        );
        doorEntity.setActive(!doorEntity.isActive());
        doorRepository.update(doorEntity);
        return doorEntity;
    }

    public void deleteDoor(UUID id) {
        doorRepository.delete(
                doorRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + " " + id)
                )
        );
    }
}
