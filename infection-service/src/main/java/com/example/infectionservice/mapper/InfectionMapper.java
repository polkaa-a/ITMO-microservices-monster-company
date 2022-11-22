package com.example.infectionservice.mapper;

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
}
