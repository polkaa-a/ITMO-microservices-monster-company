package com.example.childservice.repository;

import com.example.childservice.mapper.DoorRowMapper;
import com.example.childservice.model.DoorEntity;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.jdbc.core.namedparam.MapSqlParameterSource;
import org.springframework.jdbc.core.namedparam.NamedParameterJdbcTemplate;
import org.springframework.jdbc.core.namedparam.SqlParameterSource;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.Collections;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@RequiredArgsConstructor
@Repository
@Slf4j
public class DoorRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<DoorEntity> findById(UUID id) {
        return jdbcTemplate.queryForStream("SELECT * FROM door WHERE id = :id",
                        Collections.singletonMap("id", id),
                        new DoorRowMapper())
                .findAny();
    }

    public DoorEntity save(DoorEntity doorEntity) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", doorEntity.getId())
                .addValue("status", doorEntity.isActive());

        jdbcTemplate.update("INSERT INTO door VALUES (:id, :status)", namedParameters);
        return doorEntity;
    }

    public DoorEntity update(DoorEntity doorEntity) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", doorEntity.getId())
                .addValue("status", doorEntity.isActive());

        jdbcTemplate.update("UPDATE door SET status = :status WHERE id = :id", namedParameters);
        return doorEntity;
    }

    @Transactional
    public Page<DoorEntity> findAll(Pageable pageable, Boolean status) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM door",
                new MapSqlParameterSource(),
                Integer.class);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getOffset())
                .addValue("status", status);

        List<DoorEntity> doors;
        if (status != null) {
            doors = jdbcTemplate.query("SELECT * FROM door WHERE status = :status LIMIT :size OFFSET :offset",
                    namedParameters,
                    new DoorRowMapper());
        } else {
            doors = jdbcTemplate.query("SELECT * FROM door LIMIT :size OFFSET :offset",
                    namedParameters,
                    new DoorRowMapper());
        }
        return new PageImpl<>(doors, pageable, count);
    }

    public void delete(DoorEntity doorEntity) {
        jdbcTemplate.update("DELETE FROM door WHERE id = :id",
                Collections.singletonMap("id", doorEntity.getId()));
    }

}
