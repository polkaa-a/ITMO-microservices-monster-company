package com.example.reactiveauthendpointaggregator.dto;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class RequestFearActionDTO {

    private UUID id;
    private UUID monsterId;
    private UUID doorId;
    private Date date;

}
