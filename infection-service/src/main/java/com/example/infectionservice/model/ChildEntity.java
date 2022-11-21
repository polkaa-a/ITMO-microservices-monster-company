package com.example.infectionservice.model;

import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@AllArgsConstructor
@NoArgsConstructor
public class ChildEntity {

    private UUID id;
    private String name;
    private Date dateOfBirth;
    private String gender;
    private DoorEntity door;
}
