create table if not exists meeting
(
    id                 bigint                                       not null
        primary key auto_increment,
    number_of_recruits int                                          null,
    promise_date_time  datetime                                     null,
    created_at         timestamp(6)                                 not null,
    host_user_id       bigint                                       null,
    updated_at         timestamp(6)                                 not null,
    title              varchar(32)                                  null,
    introduction       varchar(200)                                 null,
    city               varchar(255)                                 null,
    street             varchar(255)                                 null,
    zip_code           varchar(255)                                 null,
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

