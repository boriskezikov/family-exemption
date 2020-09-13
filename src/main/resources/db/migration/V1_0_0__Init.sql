create type public.criteria_type as enum ('string', 'boolean', 'int');

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
    periodicity        varchar default ('Раз в месяц'),
    start_period       timestamp with time zone,
    end_period         timestamp with time zone,
    created            timestamp default now()
);

create table criteria
(
    id          bigserial primary key,
    type        public.criteria_type not null,
    name        varchar              not null,
    description varchar
);

create table exemption_criteria
(
    id            bigserial,
    exemption_id  bigint not null references exemption (id),
    criteria_id   bigint not null references criteria (id) on delete cascade,
    int_value     int,
    string_value  varchar,
    boolean_value boolean,
    start_period  timestamp with time zone,
    end_period    timestamp with time zone,
    created       timestamp default now(),
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

create table user_exemption
(
    user_id       bigint,
    exemption_ids bigint[],
    worksheet     varchar
);



create function create_criteria(in_criteria_type criteria_type, in_criteria_name varchar) returns bigint
    language plpgsql
as
$$
declare
    p_criteria_id bigint;
    p_table_name  varchar;
begin
    p_table_name := format('exemption_criteria_%s_%s', in_criteria_type, in_criteria_name);

    select id from public.criteria where name = upper(in_criteria_name) and type = in_criteria_type into p_criteria_id;

    if p_criteria_id is not null then
        raise exception 'Criteria %  with type % already exists', in_criteria_name, in_criteria_type;
    end if;

    insert into public.criteria(type, name)
    values (in_criteria_type, upper(in_criteria_name))
    returning id into p_criteria_id;

    execute format('CREATE TABLE public.%s PARTITION OF public.exemption_criteria FOR VALUES IN (%s);', p_table_name, p_criteria_id);
    execute format('CREATE INDEX %s_idx ON public.%s (%s_value);', p_table_name, p_table_name, in_criteria_type);
    execute format('CREATE INDEX %s_exemp_id_idx ON public.%s (exemption_id);', p_table_name, p_table_name);

    return p_criteria_id;
end;
$$;
