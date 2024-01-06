alter table users drop column oauth_authenticate_info;
alter table users add column oauth_id varchar(255) not null unique;
alter table users add column role_type varchar(255);
