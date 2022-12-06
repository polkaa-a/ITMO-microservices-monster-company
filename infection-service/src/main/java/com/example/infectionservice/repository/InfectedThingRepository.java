package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.InfectedThingRowMapper;
import com.example.infectionservice.model.InfectedThingEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Slf4j
public class InfectedThingRepository {
    private final NamedParameterJdbcTemplate jdbcTemplate;

    public InfectedThingEntity save(InfectedThingEntity infectedThingEntity) {
        infectedThingEntity.setId(UUID.randomUUID());
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", infectedThingEntity.getId())
                .addValue("name", infectedThingEntity.getName())
                .addValue("doorId", infectedThingEntity.getDoor());

        jdbcTemplate.update("INSERT INTO infected_thing VALUES (:id, :name, :doorId)",
                namedParameters);
        return infectedThingEntity;
    }

    public Optional<InfectedThingEntity> findById(UUID id) {
        return jdbcTemplate.queryForStream("SELECT * FROM infected_thing WHERE infected_thing.id = :id",
                Collections.singletonMap("id", id),
                new InfectedThingRowMapper()).findAny();
    }

    public Page<InfectedThingEntity> findAll(Pageable pageable, UUID doorId) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM infected_thing",
                new MapSqlParameterSource(),
                Integer.class);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset())
                .addValue("doorId", doorId);

        List<InfectedThingEntity> infectionThings;
        if (doorId != null) {
            infectionThings = jdbcTemplate.query("SELECT * FROM infected_thing " +
                            "WHERE door_id = :doorId LIMIT :size OFFSET :offset",
                    namedParameters,
                    new InfectedThingRowMapper());
        } else {
            infectionThings = jdbcTemplate.query("SELECT * FROM infected_thing LIMIT :size OFFSET :offset",
                    namedParameters,
                    new InfectedThingRowMapper());
        }


        return new PageImpl<>(infectionThings, pageable, count);
    }

    public void delete(InfectedThingEntity infectedThingEntity) {
        jdbcTemplate.update("DELETE FROM infected_thing WHERE id = :id",
                Collections.singletonMap("id", infectedThingEntity.getId()));
    }
}
