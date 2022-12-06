package com.example.childservice.repository;

import com.example.childservice.mapper.ChildRowMapper;
import com.example.childservice.model.ChildEntity;
import lombok.RequiredArgsConstructor;
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
public class ChildRepository {

    private final NamedParameterJdbcTemplate jdbcTemplate;

    public Optional<ChildEntity> findById(UUID id) {
        return jdbcTemplate.queryForStream("SELECT * FROM child JOIN door ON child.door_id = door.id " +
                                "WHERE child.id = :id",
                        Collections.singletonMap("id", id),
                        new ChildRowMapper())
                .findAny();
    }

    public void delete(ChildEntity childEntity) {
        jdbcTemplate.update("DELETE FROM child WHERE id = :id",
                Collections.singletonMap("id", childEntity.getId()));
    }

    public ChildEntity save(ChildEntity childEntity) {
        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("id", childEntity.getId())
                .addValue("name", childEntity.getName())
                .addValue("gender", childEntity.getGender().toString())
                .addValue("dateOfBirth", childEntity.getDateOfBirth())
                .addValue("doorId", childEntity.getDoor().getId());
        jdbcTemplate.update("INSERT INTO child VALUES (:id, :name, :gender, :dateOfBirth, :doorId)",
                namedParameters);
        return childEntity;
    }

    @Transactional
    public Page<ChildEntity> findAll(Pageable pageable) {
        Integer count = jdbcTemplate.queryForObject("SELECT count(*) FROM child",
                new MapSqlParameterSource(),
                Integer.class);

        SqlParameterSource namedParameters = new MapSqlParameterSource()
                .addValue("size", pageable.getPageSize())
                .addValue("offset", pageable.getPageSize());

        List<ChildEntity> child = jdbcTemplate.query("SELECT * FROM child " +
                        "JOIN door ON child.door_id = door.id " +
                        "LIMIT :size OFFSET :offset",
                namedParameters,
                new ChildRowMapper());
        return new PageImpl<>(child, pageable, count);
    }
}
