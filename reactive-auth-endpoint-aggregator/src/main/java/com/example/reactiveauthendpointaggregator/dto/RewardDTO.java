package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RewardDTO {

    private UUID id;
    private Integer balloonCount;
    private Integer money;

}
