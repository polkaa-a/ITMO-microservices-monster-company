CREATE TABLE IF NOT EXISTS INFECTED_THING
(
    ID      UUID PRIMARY KEY,
    NAME    VARCHAR(16) NOT NULL CHECK (NAME != ''),
    DOOR_ID UUID        NOT NULL
);

CREATE TABLE IF NOT EXISTS INFECTION
(
    ID                UUID PRIMARY KEY,
    MONSTER_ID        UUID                                                                      NOT NULL,
    INFECTED_THING_ID UUID REFERENCES INFECTED_THING (ID) ON DELETE NO ACTION ON UPDATE CASCADE NOT NULL,
    INFECTION_DATE    DATE                                                                      NOT NULL,
    CURE_DATE         DATE DEFAULT NULL,
    CHECK (CURE_DATE >= INFECTION_DATE)
);