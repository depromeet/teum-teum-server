create table if not exists users_interests
(
    users_id  bigint not null,
    interests varchar(255),
    foreign key (users_id) references users (id)
);

drop table users_street;
