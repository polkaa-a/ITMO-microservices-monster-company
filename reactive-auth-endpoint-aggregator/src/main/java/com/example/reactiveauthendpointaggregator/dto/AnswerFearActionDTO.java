package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class AnswerFearActionDTO {

    private UUID id;
    private AnswerMonsterDTO monster;
    private DoorDTO door;
    private Date date;

}
