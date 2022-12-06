package com.example.authendpointaggregator.dto;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InfectionRequestDTO {

    private UUID monsterId;

    private UUID infectedThingId;

    private Date infectionDate;

    private Date cureDate;
}
