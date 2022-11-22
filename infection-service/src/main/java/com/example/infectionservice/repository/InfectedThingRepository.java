package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.ChildMapper;
import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.model.ChildEntity;
import com.example.infectionservice.model.DoorEntity;
import com.example.infectionservice.model.InfectedThingEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class InfectedThingRepository {
    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InfectedThingRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

//    public List<InfectedThingEntity> getInfectedThings() {
//        return jdbcTemplate.query("select * from child join door on child.door_id = door.id", new InfectedThingMapper());
//    }

    public InfectedThingEntity save(InfectedThingEntity infectedThingEntity) {
        jdbcTemplate.update("insert into infected_thing values(?, ?, ?)", infectedThingEntity.getId(),
                                                                    infectedThingEntity.getName(),
                                                                    infectedThingEntity.getDoor().getId());
        return infectedThingEntity;
    }

    public Optional<InfectedThingEntity> findById(UUID id) {
        return jdbcTemplate.query("select * from infected_thing where id = ?", new Object[]{id}, new InfectedThingMapper())
                .stream().findAny();
    }

    public void delete(InfectedThingEntity infectedThingEntity) {
        jdbcTemplate.update("delete from infected_thing where id = ?", infectedThingEntity.getId());
    }
}
