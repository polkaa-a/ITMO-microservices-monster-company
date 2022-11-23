package com.example.infectionservice.service;

import com.example.infectionservice.controller.exception.NotFoundException;
import com.example.infectionservice.dto.InfectionDTO;
import com.example.infectionservice.dto.MonsterDTO;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import com.example.infectionservice.repository.InfectionRepository;
import com.example.infectionservice.service.feigh.clients.MonsterServiceFeighClient;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.sql.Date;
import java.util.Map;
import java.util.UUID;

@RequiredArgsConstructor
@Service
public class InfectionService {
    private final InfectionRepository infectionRepository;
    //private final MonsterService monsterService;
    private final MonsterServiceFeighClient monsterServiceFeighClient;
    private final InfectedThingService infectedThingService;
    private final InfectionMapper mapper;
    private static final String EXC_MES_ID = "infection not found by id ";

    public InfectionEntity save(InfectionDTO infectionDTO) {
//        MonsterEntity monsterEntity = monsterService.findById(infectionDTO.getMonsterId());
//        return infectionRepository.save(mapper.mapDtoToEntity(infectionDTO, monsterEntity));

        MonsterDTO monsterDTO = monsterServiceFeighClient.findById(infectionDTO.getMonsterId());
        InfectedThingEntity infectedThingEntity = infectedThingService.findById(infectionDTO.getInfectedThingId());
        return infectionRepository.save(mapper.mapDtoToEntity(infectionDTO, monsterDTO, infectedThingEntity));

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

    //TODO: Pageable
//    public Page<InfectionEntity> findAll(int page, int size, UUID monsterId) {
//        Pageable pageable = PageRequest.of(page, size);
//        if (monsterId != null) {
//            MonsterEntity monsterEntity = monsterService.findById(monsterId);
//            return infectionRepository.findAllByMonster(monsterEntity, pageable);
//        } else return infectionRepository.findAll(pageable);
//    }

    public void delete(UUID id) {
        infectionRepository.delete(
                infectionRepository.findById(id).orElseThrow(
                        () -> new NotFoundException(EXC_MES_ID + id)
                )
        );
    }
}
