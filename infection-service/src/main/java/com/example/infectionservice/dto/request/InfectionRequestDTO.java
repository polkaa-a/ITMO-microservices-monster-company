package com.example.infectionservice.dto.request;

import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import javax.validation.constraints.NotNull;
import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
public class InfectionRequestDTO {

    @NotNull(message = "shouldn't be null")
    private UUID monsterId;

    @NotNull(message = "shouldn't be null")
    private UUID infectedThingId;

    @NotNull(message = "shouldn't be null")
    private Date infectionDate;

    private Date cureDate;
}
