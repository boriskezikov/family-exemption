create type public.criteria_type as enum ('string', 'boolean', 'int');

alter table criteria
    add column type criteria_type;

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