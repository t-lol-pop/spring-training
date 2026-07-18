CREATE TABLE members (
    id            VARCHAR(36)  NOT NULL PRIMARY KEY,
    name          VARCHAR(100) NOT NULL,
    registered_day DATE        NOT NULL,
    status        VARCHAR(20)  NOT NULL
);
