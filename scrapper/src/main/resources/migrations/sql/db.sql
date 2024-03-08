--liquibase formatted sql

--changeset anekoss:1
-- comment: create chat table
create table if not exists chats
(
    id      bigint generated always as identity primary key,
    chat_id bigint not null unique
);
-- rollback DROP TABLE chat;


--changeset anekoss:2
-- comment: create link table
create table if not exists links
(
    id         bigint generated always as identity primary key,
    urn   text                     not null unique,
    updated_at timestamp with time zone not null,
    checked_at timestamp with time zone not null
);
-- rollback DROP TABLE link;

--changeset anekoss:3
-- comment: create chat_links table
create table if not exists chat_links
(
    id      bigint generated always as identity primary key,
    chat_id bigint not null references chats (id),
    link_id bigint not null references links (id)
);
-- rollback DROP TABLE chat_links;
