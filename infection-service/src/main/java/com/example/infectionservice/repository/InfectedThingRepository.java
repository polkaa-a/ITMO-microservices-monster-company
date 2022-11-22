package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.model.InfectedThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class InfectedThingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InfectedThingRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public List<InfectedThingEntity> getInfectedThings() {
        return jdbcTemplate.query("select * from child join door on child.door_id = door.id", new InfectedThingMapper());
    }
}
