package com.example.infectionservice.mapper;

import com.example.infectionservice.dto.InfectionDTO;
import com.example.infectionservice.dto.MonsterDTO;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@RequiredArgsConstructor
@Component
public class InfectionMapper implements RowMapper<InfectionEntity> {

    @Override
    public InfectionEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {

        InfectedThingEntity infectedThingEntity = new InfectedThingEntity();
        infectedThingEntity.setId(resultSet.getObject("infected_thing_id", java.util.UUID.class));
        infectedThingEntity.setName(resultSet.getString("name"));
        infectedThingEntity.setDoor(resultSet.getObject("door_id", java.util.UUID.class));

        InfectionEntity infectionEntity = new InfectionEntity();
        infectionEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        infectionEntity.setMonster(resultSet.getObject("monster_id", java.util.UUID.class));
        infectionEntity.setInfectedThing(infectedThingEntity);
        infectionEntity.setInfectionDate(resultSet.getDate("infection_date"));
        infectionEntity.setCureDate(resultSet.getDate("cure_date"));
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

    public InfectionEntity mapDtoToEntity(InfectionDTO infectionDTO, MonsterDTO monsterDTO, InfectedThingEntity infectedThingEntity) {
        return InfectionEntity.builder()
                .id(infectionDTO.getId())
                .monster(monsterDTO.getId())
                .infectedThing(infectedThingEntity)
                .infectionDate(infectionDTO.getInfectionDate())
                .cureDate(infectionDTO.getCureDate())
                .build();
    }
}
