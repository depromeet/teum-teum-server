create table if not exists alert(
  id bigint primary key,
  user_id bigint unique not null,
  token text not null
);
