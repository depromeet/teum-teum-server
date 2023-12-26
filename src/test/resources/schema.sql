create table if not exists users(
  certificated boolean,
  manner_temperature integer,
  mbti varchar(4),
  background_color_id bigint,
  character_id bigint,
  id bigint not null,
  birth varchar(10),
  name varchar(10),
  goal varchar(50),
  authenticated varchar(255),
  oauth_authenticate_info varchar(255) unique,
  city varchar(255),
  detail_job_class varchar(255),
  job_class varchar(255),
  job_name varchar(255),
  status enum('직장인','학생','취업준비생'),
  primary key (id)
);

create table if not exists users_interests(
  users_id bigint not null,
  interests varchar(255),
  foreign key (users_id) references users(id)
);

create table if not exists users_street(
  users_id bigint not null,
  street varchar(255),
  foreign key (users_id) references users(id)
);
