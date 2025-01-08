
create table Image
(
    image_id    int auto_increment
        primary key,
    task_id     int          not null,
    image_url   varchar(255) not null,
    image_order int          not null,
    constraint Image_ibfk_1
        foreign key (task_id) references Task (task_id)
            on delete cascade
);

create index task_id
    on Image (task_id);