package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerElectricBalloonDTO {

    private UUID id;
    private AnswerFearActionDTO fearAction;
    private CityDTO city;
}
