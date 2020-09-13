drop table user_exemption;

create table user_exemption
(
    user_id       bigint,
    exemption_ids bigint[],
    worksheet     varchar,
    constraint unique_user_data unique (user_id, exemption_ids, worksheet)
);