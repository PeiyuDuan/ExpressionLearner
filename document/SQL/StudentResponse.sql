
create table StudentResponse
(
    response_id     int auto_increment
        primary key,
    student_id      int                                 null,
    task_id         int                                 null,
    response_text   text                                null,
    submission_date timestamp default CURRENT_TIMESTAMP null,
    ai_feedback     json                                not null,
    constraint StudentResponse_ibfk_1
        foreign key (student_id) references User (user_id)
            on delete cascade,
    constraint StudentResponse_ibfk_2
        foreign key (task_id) references Task (task_id)
            on delete cascade
);

create index student_id
    on StudentResponse (student_id);

create index task_id
    on StudentResponse (task_id);