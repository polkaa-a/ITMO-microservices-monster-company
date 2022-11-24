package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.InfectedThingDTO;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.repository.InfectedThingRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InfectedThingService {

    private final InfectedThingRepository infectedThingRepository;
    private final InfectedThingMapper mapper;

    private static final String EXC_MES_ID = "none infected thing was found by id ";

    public InfectedThingEntity save(InfectedThingDTO infectedThingDTO) {
        infectedThingDTO.setId(UUID.randomUUID());
        return infectedThingRepository.save(mapper.mapDtoToEntity(infectedThingDTO));
    }

    public InfectedThingEntity findById(UUID infectedThingId) {
        return infectedThingRepository.findById(infectedThingId).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + infectedThingId)
        );
    }

    public Page<InfectedThingEntity> findAll(int page, int size, UUID doorId) {
        Pageable pageable = PageRequest.of(page, size);
        if (doorId != null) {
            return infectedThingRepository.findAllByDoor(doorId, pageable);
        } else return infectedThingRepository.findAll(pageable);
    }

    public void delete(UUID infectedThingId) {
        infectedThingRepository.delete(
                infectedThingRepository.findById(infectedThingId).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + ": " + infectedThingId)
                )
        );
    }
}
