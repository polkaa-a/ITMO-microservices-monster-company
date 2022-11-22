package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.InfectionDTO;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class InfectionMapper implements RowMapper<InfectionEntity> {

    //todo: хз как это делать потому что нужно создавать кучу объектов
    @Override
    public InfectionEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        InfectionEntity infectionEntity = new InfectionEntity();
        return infectionEntity;
    }

    public InfectionDTO mapEntityToDto(InfectionEntity infectionEntity) {
        return InfectionDTO.builder()
                .id(infectionEntity.getId())
                .monsterId(infectionEntity.getMonster())
                .infectedThingId(infectionEntity.getInfectedThing().getId())
                .infectionDate(infectionEntity.getInfectionDate())
                .cureDate(infectionEntity.getCureDate())
                .build();
    }

//    public InfectionEntity mapDtoToEntity(InfectionDTO infectionDTO, MonsterEntity monsterEntity) {
//        return InfectionEntity.builder()
//                .id(infectionDTO.getId())
//                .monster(monsterEntity)
//                .infectedThing(infectedThingService.findById(infectionDTO.getInfectedThingId()))
//                .infectionDate(infectionDTO.getInfectionDate())
//                .cureDate(infectionDTO.getCureDate())
//                .build();
//    }
}
