package com.example.infectionservice.repository;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Repository;

import javax.inject.Qualifier;
import javax.sql.DataSource;
@Repository
public class ChildRepository {

    private final JdbcTemplate jdbcTemplate;

    @Autowired
    public ChildRepository (JdbcTemplate jdbcTemplate){
        this.jdbcTemplate = jdbcTemplate;
    }

}
