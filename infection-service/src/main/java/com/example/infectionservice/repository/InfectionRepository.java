package com.example.infectionservice.repository;

import com.example.infectionservice.mapper.InfectionRowMapper;
import com.example.infectionservice.model.InfectionEntity;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;

import java.sql.Date;
import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
public class InfectionRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<InfectionEntity> findById(UUID id) {
        return jdbcTemplate.queryForStream("SELECT * FROM infection " +
                        "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                        "WHERE infection.id = :id",
                Collections.singletonMap("id", id),
                new InfectionRowMapper()).findAny();
    }

    public InfectionEntity save(InfectionEntity infectionEntity) {
        infectionEntity.setId(UUID.randomUUID());
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", infectionEntity.getId())
                .addValue("monsterId", infectionEntity.getMonster())
                .addValue("thingId", infectionEntity.getInfectedThing().getId())
                .addValue("infectionDate", infectionEntity.getInfectionDate())
                .addValue("cureDate", infectionEntity.getCureDate());

        jdbcTemplate.update("INSERT INTO infection VALUES (:id, :monsterId, :thingId, :infectionDate, :cureDate)",
                namedParameters);
        return infectionEntity;
    }

    public InfectionEntity update(InfectionEntity infectionEntity) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("cureDate", infectionEntity.getCureDate())
                .addValue("id", infectionEntity.getId());

        jdbcTemplate.update("UPDATE infection SET cure_date = :cureDate WHERE id = :id",
                namedParameters);
        return infectionEntity;
    }

    public Page<InfectionEntity> findAll(Pageable pageable, UUID monsterId, Date date) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM infection",
                new MapSqlParameterSource(),
                Integer.class);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset())
                .addValue("monsterId", monsterId)
                .addValue("date", date);

        List<InfectionEntity> infections;
        if (monsterId != null) {
            infections = jdbcTemplate.query("SELECT * FROM infection " +
                            "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                            "WHERE monster_id = :monsterId LIMIT :size OFFSET :offset",
                    namedParameters,
                    new InfectionRowMapper());
        } else if (date != null) {
            infections = jdbcTemplate.query("SELECT * FROM infection " +
                            "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                            "WHERE infection_date = :date LIMIT :size OFFSET :offset",
                    namedParameters,
                    new InfectionRowMapper());
        } else {
            infections = jdbcTemplate.query("SELECT * FROM infection " +
                            "JOIN infected_thing ON infection.infected_thing_id = infected_thing.id " +
                            "LIMIT :size OFFSET :offset",
                    namedParameters,
                    new InfectionRowMapper());
        }

        return new PageImpl<>(infections, pageable, count);
    }

    public List<InfectionEntity> findAllByDate(Date date) {
        return jdbcTemplate.query("SELECT * FROM infection WHERE infection_date = :date",
                Collections.singletonMap("date", date),
                new InfectionRowMapper()
        );
    }

    public void delete(InfectionEntity infectionEntity) {
        jdbcTemplate.update("DELETE FROM infection WHERE id = :id",
                Collections.singletonMap("id", infectionEntity.getId()));
    }
}
