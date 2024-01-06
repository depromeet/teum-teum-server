alter table users
change column oauth_authenticate_info oauthd_id varchar(255) not null, unique;

alter table users
add column role_type varchar(255);