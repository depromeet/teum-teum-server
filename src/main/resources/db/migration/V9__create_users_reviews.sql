create table if not exists users_reviews(
    users_id bigint not null auto_increment,
    reviews enum('별로에요','좋아요','최고에요'),
    foreign key (users_id) references users(id)
    on delete cascade
);
