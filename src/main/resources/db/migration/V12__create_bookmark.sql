create table if not exists meeting_bookmarked_user_ids
(
    meeting_id          bigint not null,
    bookmarked_user_ids bigint null,
    foreign key (meeting_id) references meeting (id)
);
