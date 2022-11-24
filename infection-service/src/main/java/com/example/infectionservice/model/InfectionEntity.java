package com.example.infectionservice.model;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfectionEntity {
    private UUID id;
    private UUID monster;
    private InfectedThingEntity infectedThing;
    private Date infectionDate;
    private Date cureDate;

}
