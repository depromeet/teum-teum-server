alter table alert
    drop column userId;
alter table alert
    add column user_id bigint not null;
