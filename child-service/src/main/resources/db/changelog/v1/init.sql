CREATE TYPE GENDER AS ENUM ('male', 'female');

CREATE TABLE IF NOT EXISTS DOOR
(
    ID     UUID PRIMARY KEY,
    STATUS BOOLEAN NOT NULL
);

CREATE TABLE IF NOT EXISTS CHILD
(
    ID            UUID PRIMARY KEY,
    NAME          VARCHAR(16)                                                   NOT NULL,
    GENDER        VARCHAR(6)                                                    NOT NULL,
    DATE_OF_BIRTH DATE                                                          NOT NULL,
    DOOR_ID       UUID REFERENCES DOOR (ID) ON DELETE CASCADE ON UPDATE CASCADE NOT NULL,
    CHECK ((NAME != '') AND (DATE_OF_BIRTH < CURRENT_DATE))
);