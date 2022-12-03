package com.example.childservice.service;

import com.example.childservice.controller.exception.NotFoundException;
import com.example.childservice.model.DoorEntity;
import com.example.childservice.repository.DoorRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class DoorService {

    private static final String EXC_MES_ID = "none door was found by id";
    private final DoorRepository doorRepository;

    public DoorEntity findById(UUID doorId) {
        return doorRepository.findById(doorId).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + ": " + doorId)
        );
    }

    public DoorEntity save(DoorEntity doorEntity) {
        doorEntity.setId(UUID.randomUUID());
        return doorRepository.save(doorEntity);
    }

    public Page<DoorEntity> findAll(Pageable pageable, Boolean status) {
        return doorRepository.findAll(pageable, status);
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
