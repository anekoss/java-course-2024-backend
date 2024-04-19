--liquibase formatted sql

--changeset anekoss:9
-- comment: insert github_links table values
insert into github_links(link_id, branch_count) values (1, 2);


--changeset anekoss:10
-- comment: insert stackoverflow_links table values
insert into stackoverflow_links(link_id, answer_count) values (2, 3);









