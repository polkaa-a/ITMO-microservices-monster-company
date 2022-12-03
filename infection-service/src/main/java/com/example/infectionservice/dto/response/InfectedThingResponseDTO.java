package com.example.infectionservice.dto.response;

import com.example.infectionservice.dto.DoorDTO;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.util.UUID;

@Getter
@Setter
@Builder
public class InfectedThingResponseDTO {
    private UUID id;

    private String name;

    private DoorDTO door;
}
