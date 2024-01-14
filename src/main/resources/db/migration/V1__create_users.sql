create table if not exists users(
    id bigint not null auto_increment,
    certificated boolean,
    manner_temperature integer,
    mbti varchar(4),
    character_id bigint,
    birth varchar(10),
    name varchar(10),
    goal varchar(50),
    authenticated enum ('카카오','네이버') not null,
    oauth_authenticate_info varchar(255) unique,
    city varchar(255),
    detail_job_class varchar(255),
    job_class varchar(255),
    job_name varchar(255),
    status enum('직장인','학생','취업준비생'),
    terms_of_service boolean not null,
    privacy_policy boolean not null,
    created_at timestamp(6) not null,
    updated_at timestamp(6) not null,
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
