package com.example.childservice.mapper;

import com.example.childservice.enums.Gender;
import com.example.childservice.model.ChildEntity;
import com.example.childservice.model.DoorEntity;
import org.springframework.jdbc.core.RowMapper;
import org.springframework.stereotype.Component;

import java.sql.ResultSet;
import java.sql.SQLException;

@Component
public class ChildRowMapper implements RowMapper<ChildEntity> {
    @Override
    public ChildEntity mapRow(ResultSet resultSet, int rowNum) throws SQLException {
        ChildEntity childEntity = new ChildEntity();
        childEntity.setId(resultSet.getObject("id", java.util.UUID.class));
        childEntity.setName(resultSet.getString("name"));
        childEntity.setDateOfBirth(resultSet.getDate("date_of_birth"));
        childEntity.setGender(Gender.valueOf(resultSet.getString("gender")));
        DoorEntity doorEntity = new DoorEntity();
        doorEntity.setId(resultSet.getObject("door_id", java.util.UUID.class));
        doorEntity.setActive(resultSet.getBoolean("status"));
        childEntity.setDoor(doorEntity);
        return childEntity;
    }
}
