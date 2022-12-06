package com.example.childservice.model;

import com.example.childservice.enums.Gender;
import lombok.*;

import java.sql.Date;
import java.util.UUID;

@Getter
@Setter
@ToString
@Builder
@AllArgsConstructor
@NoArgsConstructor
public class ChildEntity {
    private UUID id;
    private String name;
    private Date dateOfBirth;
    private Gender gender;
    private DoorEntity door;
}
