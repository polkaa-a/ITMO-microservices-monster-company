package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.InfectedThingMapper;
import com.example.infectionservice.mapper.InfectionMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import com.example.infectionservice.model.InfectionEntity;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.UUID;

@Repository
public class InfectionRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public InfectionRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

    public Optional<InfectionEntity> findById(UUID id) {
        return jdbcTemplate.query("select * from infection where id = ?", new Object[]{id}, new InfectionMapper())
                .stream().findAny();
    }

    public InfectionEntity save(InfectionEntity infectionEntity) {
        jdbcTemplate.update("insert into infection values(?, ?, ?, ?, ?)", infectionEntity.getId(),
                infectionEntity.getMonster(),
                infectionEntity.getInfectedThing(),
                infectionEntity.getInfectionDate(),
                infectionEntity.getCureDate());
        return infectionEntity;
    }

    public void delete(InfectionEntity infectionEntity) {
        jdbcTemplate.update("delete from infection where id = ?", infectionEntity.getId());
    }
}
