create table if not exists withdraw_reason
(
    id              bigint       not null auto_increment,
    withdraw_reason varchar(30)  not null,
    created_at      timestamp(6) not null,
    updated_at      timestamp(6) not null,
    primary key (id)
);
