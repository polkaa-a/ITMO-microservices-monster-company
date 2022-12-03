package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class CityDTO {

    private UUID id;
    private String name;
}
