package com.example.authendpointaggregator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InfectionResponseDTO {
    private UUID id;
    private MonsterDTO monster;
    private InfectedThingResponseDTO infectedThing;
    private Date infectionDate;
    private Date cureDate;
}
