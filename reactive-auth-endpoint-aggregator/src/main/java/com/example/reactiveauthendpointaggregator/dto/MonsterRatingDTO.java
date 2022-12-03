package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class MonsterRatingDTO {

    private UUID monsterID;
    private Long countBalloons;
}
