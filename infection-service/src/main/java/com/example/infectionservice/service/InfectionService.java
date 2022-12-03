package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.request.InfectionRequestDTO;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import com.example.infectionservice.repository.InfectionRepository;
import com.example.infectionservice.service.feign.clients.MonsterServiceFeignClient;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InfectionService {
    private static final String EXC_MES_ID = "infection not found by id ";
    private final InfectionRepository infectionRepository;
    private final MonsterServiceFeignClient monsterServiceFeignClient;
    private final InfectedThingService infectedThingService;
    private final InfectionMapper mapper;

    public InfectionEntity save(InfectionRequestDTO infectionRequestDTO) {
        monsterServiceFeignClient.findById(infectionRequestDTO.getMonsterId());

        InfectedThingEntity infectedThingEntity = infectedThingService.findById(infectionRequestDTO.getInfectedThingId());
        return infectionRepository.save(mapper.mapDtoToEntity(infectionRequestDTO, infectedThingEntity));

    }

    public InfectionEntity findById(UUID id) {
        return infectionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + id)
        );
    }

    public InfectionEntity updateCureDate(UUID id, Map<String, Date> cureDate) {
        InfectionEntity infectionEntity = infectionRepository.findById(id).orElseThrow(
                () -> new NotFoundException(EXC_MES_ID + id)
        );
        infectionEntity.setCureDate(cureDate.get("cureDate"));
        infectionRepository.update(infectionEntity);
        return infectionEntity;
    }

    public Page<InfectionEntity> findAll(Pageable pageable, UUID monsterId, Date date) {
        return infectionRepository.findAll(pageable, monsterId, date);
    }

    public void delete(UUID id) {
        infectionRepository.delete(
                infectionRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + id)
                )
        );
    }
}
