drop database if exists rocket_order;
create database rocket_order character set utf8mb4;

use rocket_order;
drop table if exists t_order;
create table t_order(
    order_id varchar(256) not null default '' primary key,
    order_type varchar(256) not null default '',
    city_id varchar(256) not null default '',
    platform_id varchar(256) not null default '',
    user_id varchar(256) not null default '',
    supplier_id varchar(256) not null default '',
    goods_id varchar(256) not null default '',
    order_status varchar(256) not null default '',
    remark varchar(256) not null default '',
    create_by varchar(256) not null default '',
    create_time datetime not null default current_timestamp,
    update_by varchar(256) not null default '',
    update_time datetime not null default current_timestamp
) engine = InnoDB default charset = utf8mb4;

drop database if exists rocket_store;
create database rocket_store character set utf8mb4;

use rocket_store;
drop table if exists t_store;
create table t_store(
    store_id varchar(256) not null default '' primary key,
    goods_id varchar(256) not null default '',
    supplier_id varchar(256) not null default '',
    goods_name varchar(256) not null default '',
    store_count int unsigned not null default 0,
    version int unsigned not null default 0,
    create_by varchar(256) not null default '',
    create_time datetime not null default current_timestamp,
    update_by varchar(256) not null default '',
    update_time datetime not null default current_timestamp
) engine = InnoDB default charset = utf8mb4;