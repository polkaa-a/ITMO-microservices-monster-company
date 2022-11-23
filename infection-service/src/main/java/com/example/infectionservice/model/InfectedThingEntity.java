package com.example.infectionservice.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class InfectedThingEntity {

    private UUID id;
    private String name;
    private DoorEntity door;
}
