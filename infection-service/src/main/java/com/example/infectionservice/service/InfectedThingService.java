package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.request.InfectedThingRequestDTO;
import com.example.infectionservice.dto.response.InfectedThingResponseDTO;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.repository.InfectedThingRepository;
import com.example.infectionservice.service.feign.clients.DoorServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InfectedThingService {

    private static final String EXC_MES_ID = "none infected thing was found by id ";
    private final InfectedThingRepository infectedThingRepository;
    private final InfectedThingMapper mapper;
    private final DoorServiceFeignClient doorServiceFeignClient;

    public InfectedThingResponseDTO save(InfectedThingRequestDTO infectedThingRequestDTO) {
        doorServiceFeignClient.findById(infectedThingRequestDTO.getDoorId());

        return mapper.mapEntityToDto(infectedThingRepository
                .save(mapper.mapDtoToEntity(infectedThingRequestDTO)));
    }

    public InfectedThingEntity findById(UUID infectedThingId) {
        return infectedThingRepository.findById(infectedThingId).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + infectedThingId)
        );
    }

    public Page<InfectedThingEntity> findAll(Pageable pageable, UUID doorId) {
        return infectedThingRepository.findAll(pageable, doorId);
    }

    public void delete(UUID infectedThingId) {
        infectedThingRepository.delete(
                infectedThingRepository.findById(infectedThingId).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + ": " + infectedThingId)
                )
        );
    }
}
