package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.request.InfectedThingRequestDTO;
import com.example.infectionservice.dto.response.InfectedThingResponseDTO;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.service.feign.clients.DoorServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InfectedThingMapper {
    private final DoorServiceFeignClient doorServiceFeignClient;

    public InfectedThingResponseDTO mapEntityToDto(InfectedThingEntity infectedThingEntity) {
        return InfectedThingResponseDTO.builder()
                .id(infectedThingEntity.getId())
                .name(infectedThingEntity.getName())
                .door(doorServiceFeignClient.findById(infectedThingEntity.getDoor()))
                .build();
    }

    public InfectedThingEntity mapDtoToEntity(InfectedThingRequestDTO infectedThingRequestDTO) {
        return InfectedThingEntity.builder()
                .name(infectedThingRequestDTO.getName())
                .door(infectedThingRequestDTO.getDoorId())
                .build();
    }
}
