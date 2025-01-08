
create table UserTaskStages
(
    user_id     int                                 not null,
    task_id     int                                 not null,
    stage       int                                 not null,
    update_time timestamp default CURRENT_TIMESTAMP null on update CURRENT_TIMESTAMP,
    constraint user_id
        unique (user_id, task_id, stage),
    constraint UserTaskStages_ibfk_1
        foreign key (user_id) references User (user_id)
            on delete cascade,
    constraint UserTaskStages_ibfk_2
        foreign key (task_id) references Task (task_id)
            on delete cascade
);

create index task_id
    on UserTaskStages (task_id);