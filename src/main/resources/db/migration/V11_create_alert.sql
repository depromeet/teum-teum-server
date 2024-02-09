create table if not exists alert(
    id bigint not null auto_increment,
    userId bigint not null,
    title varchar(20) not null,
    `body` varchar(20) not null,
    type enum('BEFORE_MEETING'),
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
    primary key (id)
);
