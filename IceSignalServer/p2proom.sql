drop database if exists room;
create database room character set utf8;
use room;

create table tbl_room
(
    `id`    integer unsigned not null auto_increment primary key,
    `room_id`  varchar(64)  not null,
    `room_type`  tinyint  default 1,
    `media_type` tinyint  default 1,
    `status` tinyint  default 1,
    `creator` varchar(64)  not null,
    `create_time` datetime,
    `due_time` datetime
);


create table tbl_room_user
(
    `id`        integer not null auto_increment primary key,
    `room_id`    varchar(64) not null,
    `user_id`    varchar(64) not null,
    `status` tinyint  default 1,
    `enter_time` datetime,
    `leave_time` datetime
);

create table tbl_user_msg
(
    `id`        integer not null auto_increment primary key,
    `user_id`    varchar(64) not null,
    `msg_type`  tinyint not null,
    `msg_cnt`   varchar(2048)
);

grant all on room.* to p2p;
