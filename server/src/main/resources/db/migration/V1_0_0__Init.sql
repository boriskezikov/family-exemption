create table document
(
    id   bigserial primary key,
    name varchar,
    data bytea
);

create table exemption
(
    id                 bigserial primary key,
    name               varchar  not null,
    description        varchar,
    reference_document bigint[] not null,
    start_period       timestamp with time zone,
    end_period         timestamp with time zone,
    created            timestamp
);

create table criteria
(
    id          bigserial primary key,
    name        varchar not null,
    description varchar
);

create table exemption_criteria
(
    id           bigserial,
    exemption_id bigint not null references exemption (id),
    criteria_id  bigint not null references criteria (id) on delete cascade,
    int_value    int,
    string_value varchar,
    start_period timestamp with time zone,
    end_period   timestamp with time zone,
    created      timestamp,
    CONSTRAINT unique_exemption_id_per_criteria_id UNIQUE (exemption_id, criteria_id)
) partition by list (criteria_id);

create index on exemption_criteria (criteria_id);

create table exempted_category
(
    id           bigserial primary key,
    name         varchar  not null,
    description  varchar,
    criteria_ids bigint[] not null
);

