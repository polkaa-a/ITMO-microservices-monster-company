package com.example.infectionservice.mapper;

import com.example.infectionservice.model.ChildEntity;
import org.springframework.jdbc.core.RowMapper;

import java.sql.ResultSet;
import java.sql.SQLException;

public class ChildMapper implements RowMapper {
    @Override
    public Object mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ChildEntity childEntity = new ChildEntity();
        childEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        childEntity.setName(resultSet.getString("name"));
        childEntity.setDateOfBirth(resultSet.getDate("date_of_birth"));
        childEntity.setGender(resultSet.getString("gender"));
        //childEntity.setDoor(resultSet.getObject("door_id", java.util.UUID.class));

        return null;
    }
}
