package com.example.infectionservice.mapper;

import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class InfectionRowMapper implements RowMapper<InfectionEntity> {
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
}
