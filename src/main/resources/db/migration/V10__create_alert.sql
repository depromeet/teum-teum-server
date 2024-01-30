create table if not exists user_alert(
  id bigint primary key auto_increment,
  user_id bigint unique not null,
  token text not null
);

create index user_alert_idx_user_id on user_alert(user_id);
