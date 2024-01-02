create table if not exists users_friends(
  users_id               bigint not null,
  friends                bigint not null,
  foreign key (users_id) references users(id)
);
