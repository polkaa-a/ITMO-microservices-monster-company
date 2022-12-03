package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.request.InfectionRequestDTO;
import com.example.infectionservice.dto.response.InfectionResponseDTO;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import com.example.infectionservice.service.feign.clients.MonsterServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@RequiredArgsConstructor
@Component
public class InfectionMapper {
    private final MonsterServiceFeignClient monsterServiceFeignClient;
    private final InfectedThingMapper infectedThingMapper;

    public InfectionResponseDTO mapEntityToDto(InfectionEntity infectionEntity) {
        return InfectionResponseDTO.builder()
                .id(infectionEntity.getId())
                .monster(monsterServiceFeignClient.findById(infectionEntity.getMonster()))
                .infectedThing(infectedThingMapper.mapEntityToDto(infectionEntity.getInfectedThing()))
                .infectionDate(infectionEntity.getInfectionDate())
                .cureDate(infectionEntity.getCureDate())
                .build();
    }

    public InfectionEntity mapDtoToEntity(InfectionRequestDTO infectionRequestDTO, InfectedThingEntity infectedThingEntity) {
        return InfectionEntity.builder()
                .monster(infectionRequestDTO.getMonsterId())
                .infectedThing(infectedThingEntity)
                .infectionDate(infectionRequestDTO.getInfectionDate())
                .cureDate(infectionRequestDTO.getCureDate())
                .build();
    }
}
