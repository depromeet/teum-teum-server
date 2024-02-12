alter table alert drop column title;
alter table alert drop column `body`;
alter table alert add column title text not null;
alter table alert add column `body` text not null;
