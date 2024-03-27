--liquibase formatted sql

--changeset anekoss:4
-- comment: create link table
create table if not exists stackoverflow_links
(
    id         bigint generated always as identity primary key,
    link_id bigint not null unique references links (id) on delete cascade,
    answer_count bigint not null
);
-- rollback DROP TABLE link;
