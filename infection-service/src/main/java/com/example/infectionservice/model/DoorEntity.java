package com.example.infectionservice.model;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class DoorEntity {

    private UUID id;
    private boolean isActive;
}
