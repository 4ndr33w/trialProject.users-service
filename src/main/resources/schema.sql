drop table if exists users cascade;
drop table if exists tokenvault cascade;

CREATE TABLE IF NOT EXISTS users
(
    id            bigserial PRIMARY KEY,
    name          text,
    lastname      text,
    email         text unique not null,
    password      text        not null,
    phone         text,
    created       timestamptz default now(),
    updated       timestamptz,
    image         bytea,
    lastlogindate timestamptz,
    userstatus    integer     default 1,
    username      text
);

create table tokenvault
(
    id       bigserial primary key,
    token    text,
    created  timestamptz default now(),
    expired  timestamptz,
    username text,
    userid   bigint
);

