alter table alert drop column type;
alter table alert add column type enum('BEFORE_MEETING', 'END_MEETING', 'RECOMMEND_USER') not null;
