--liquibase formatted sql

--changeset anekoss:2
-- comment: create linkEntity table
create table if not exists linkEntities
(
    id         bigint generated always as identity primary key,
    uri   text                     not null unique,
    link_type text not null,
    updated_at timestamp not null,
    checked_at timestamp not null
);
-- rollback DROP TABLE linkEntity;
