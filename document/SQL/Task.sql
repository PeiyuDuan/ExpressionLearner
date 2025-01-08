
create table Task
(
    task_id           int auto_increment
        primary key,
    task_prompt       json                                  not null,
    creation_date     timestamp   default CURRENT_TIMESTAMP null,
    image_description varchar(255)                          not null,
    task_name         varchar(50) default '看图说话'        not null
);