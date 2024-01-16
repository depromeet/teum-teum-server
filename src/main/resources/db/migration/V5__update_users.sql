drop table users_interests;

alter table users
    drop column city;

alter table users
    add column activity_area varchar(255);

