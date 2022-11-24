package com.example.infectionservice.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class InfectedThingEntity {

    private UUID id;
    private String name;
    private UUID door;

}
