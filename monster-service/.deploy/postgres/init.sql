CREATE TYPE GENDER AS ENUM ('male', 'female');
CREATE TYPE JOB AS ENUM ('scarer', 'cleaner','scare assistant','disinfector','recruiter');

CREATE TABLE IF NOT EXISTS MONSTER
(
    ID            UUID PRIMARY KEY,
    NAME          VARCHAR(16)                                                    NOT NULL,
    GENDER        VARCHAR(6)                                                     NOT NULL,
    DATE_OF_BIRTH DATE                                                           NOT NULL,
    JOB           VARCHAR(30)                                                    NOT NULL,
    EMAIL         VARCHAR(30)                                                    NOT NULL UNIQUE,
    SALARY        INT                                                            NOT NULL,
    USER_ID       UUID                                                           NOT NULL UNIQUE,
    CHECK ((NAME != '') AND (Email != '') AND (SALARY > 0) AND (DATE_OF_BIRTH < CURRENT_DATE))
);

CREATE TABLE IF NOT EXISTS REWARD
(
    ID            UUID PRIMARY KEY,
    BALLOON_COUNT INT NOT NULL UNIQUE,
    MONEY         INT NOT NULL,
    CHECK ((BALLOON_COUNT > 0) AND (MONEY > 0))
);

CREATE TABLE IF NOT EXISTS MONSTER_REWARD
(
    MONSTER_ID UUID REFERENCES MONSTER (ID) ON DELETE NO ACTION ON UPDATE CASCADE,
    REWARD_ID  UUID REFERENCES REWARD (ID) ON DELETE NO ACTION ON UPDATE CASCADE,
    PRIMARY KEY (MONSTER_ID, REWARD_ID)
);

CREATE TABLE IF NOT EXISTS FEAR_ACTION
(
    ID         UUID PRIMARY KEY,
    MONSTER_ID UUID REFERENCES MONSTER (ID) ON DELETE NO ACTION ON UPDATE CASCADE NOT NULL,
    DOOR_ID    UUID                                                               NOT NULL,
    DATE       DATE                                                               NOT NULL
);

CREATE TABLE IF NOT EXISTS CITY
(
    ID   UUID PRIMARY KEY,
    NAME VARCHAR(20) NOT NULL UNIQUE CHECK (NAME != '')
);

CREATE TABLE IF NOT EXISTS ELECTRIC_BALLOON
(
    ID             UUID PRIMARY KEY,
    FEAR_ACTION_ID UUID REFERENCES FEAR_ACTION (ID),
    CITY_ID        UUID REFERENCES CITY (ID) NOT NULL
);
