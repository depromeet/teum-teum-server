alter table alert drop column created_at;
alter table alert drop column updated_at;
alter table alert add column created_at datetime not null;
alter table alert add column updated_at datetime not null;
