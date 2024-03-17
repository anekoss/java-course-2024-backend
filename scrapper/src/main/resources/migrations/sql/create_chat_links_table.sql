--liquibase formatted sql

--changeset anekoss:3
-- comment: create chat_links table
create table if not exists chat_links
(
    id      bigint generated always as identity primary key,
    chat_id bigint not null references chats (id),
    link_id bigint not null references links (id)
    );
-- rollback DROP TABLE chat_links;
