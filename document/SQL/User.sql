
create table User
(
    user_id           int auto_increment
        primary key,
    username          varchar(255)                        not null,
    password_hash     varchar(255)                        not null,
    email             varchar(255)                        null,
    registration_date timestamp default CURRENT_TIMESTAMP null,
    avatar            text                                null
);