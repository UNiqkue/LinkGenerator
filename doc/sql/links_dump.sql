create table links
(
    short_link    varchar(10) not null
        primary key,
    original_link varchar(2048),
    visits_count  bigint,
    create_at     timestamp
);
