package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestElectricBalloonDTO {

    private UUID id;
    private UUID fearActionId;
    private UUID cityId;
}
