create table if not exists users
(
    id                 bigint             not null auto_increment,
    certificated       boolean,
    manner_temperature integer,
    mbti               varchar(4),
    character_id       bigint,
    birth              varchar(10),
    name               varchar(10),
    goal               varchar(50),
    oauth_id           varchar(255)       not null unique,
    authenticated      enum ('카카오','네이버') not null,
    role_type          enum ('ROLE_USER','ROLE_ADMIN'),
    activity_area      varchar(255),
    detail_job_class   varchar(255),
    job_class          varchar(255),
    job_name           varchar(255),
    status             enum ('직장인','학생','취업준비생'),
    terms_of_service   boolean            not null,
    privacy_policy     boolean            not null,
    created_at         timestamp(6)       not null,
    updated_at         timestamp(6)       not null,
    primary key (id)
);

create table if not exists users_interests
(
    users_id  bigint not null,
    interests varchar(255),
    foreign key (users_id) references users (id)
);


create table if not exists meeting
(
    id                 bigint                                       not null
        primary key auto_increment,
    host_user_id       bigint                                       null,
    number_of_recruits int                                          null,
    promise_date_time  datetime                                     null,
    created_at         timestamp(6)                                 not null,
    updated_at         timestamp(6)                                 not null,
    title              varchar(32)                                  null,
    introduction       varchar(200)                                 null,
    address            varchar(255)                                 null,
    main_street        varchar(255)                                 null,
    address_detail     varchar(255)                                 null,
    topic              enum ('고민_나누기', '모여서_작업', '스터디', '사이드_프로젝트') null
);

create table if not exists meeting_image_urls
(
    meeting_id bigint       not null,
    image_urls varchar(255) null,
    foreign key (meeting_id) references meeting (id)
);

create table if not exists meeting_participant_user_ids
(
    meeting_id           bigint not null,
    participant_user_ids bigint null,
    foreign key (meeting_id) references meeting (id)
);

create table if not exists users_friends
(
    users_id bigint not null,
    friends  bigint not null,
    foreign key (users_id) references users (id)
);

create table if not exists withdraw_reasons
(
    id              bigint       not null auto_increment,
    withdraw_reason varchar(30)  not null,
    created_at      timestamp(6) not null,
    updated_at      timestamp(6) not null,
    primary key (id)
);

create table if not exists user_alert(
  id bigint primary key auto_increment,
  user_id bigint unique not null,
  token text not null
);

create index if not exists user_alert_idx_user_id on user_alert(user_id);

create table if not exists users_reviews
(
    users_id bigint not null auto_increment,
    reviews  enum ('별로에요','좋아요','최고에요'),
    foreign key (users_id) references users (id)
    on delete cascade
);

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

create table if not exists meeting_bookmarked_user_ids
(
    meeting_id          bigint not null,
    bookmarked_user_ids bigint null,
    foreign key (meeting_id) references meeting (id)
);
