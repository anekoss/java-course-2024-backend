--liquibase formatted sql

--changeset anekoss:5
-- comment: create link table
create table if not exists github_links
(
    id         bigint generated always as identity primary key,
    link_id bigint not null references links (id) on delete cascade,
    branch text not null
    );
-- rollback DROP TABLE link;
