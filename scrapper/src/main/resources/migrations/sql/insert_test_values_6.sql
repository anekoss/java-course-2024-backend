--liquibase formatted sql

--changeset anekoss:6
-- comment: insert tg_chats table values
insert into tg_chats(chat_id) values (124025);
insert into tg_chats(chat_id) values (327034);
insert into tg_chats(chat_id) values (444444);
insert into tg_chats(chat_id) values (555555);



--changeset anekoss:7
-- comment: insert links table values
insert into links(uri, link_type, updated_at, checked_at) values ('https://github.com/anekoss/tinkoff','GITHUB', '2024-01-01 10:00:00+00', '2024-01-01 13:00:00+00');
insert into links(uri, link_type, updated_at, checked_at) values ('https://stackoverflow.com/questions/59339862/retrieving-text-body-of-answers-and-comments-using-stackexchange-api', 'STACKOVERFLOW', '2024-02-01 13:00:54+00', '2024-02-05 13:00:00+00');
insert into links(uri, link_type, updated_at, checked_at) values ('https://stackoverflow.com/questions/44760112/marching-cubes-generating-holes-in-mesh', 'STACKOVERFLOW', '2024-03-27 13:00:54+00', '2024-03-26 13:00:00+00');


--changeset anekoss:8
-- comment: insert tg_chat_links table values
insert into tg_chat_links(tg_chat_id, link_id) values (1, 1);
insert into tg_chat_links(tg_chat_id, link_id) values (1, 2);
insert into tg_chat_links(tg_chat_id, link_id) values (1, 3);
insert into tg_chat_links(tg_chat_id, link_id) values (2, 1);
insert into tg_chat_links(tg_chat_id, link_id) values (2, 2);
insert into tg_chat_links(tg_chat_id, link_id) values (3, 1);

--changeset anekoss:9
-- comment: insert github_links table values
insert into github_links(link_id, branch_count) values (1, 2);


--changeset anekoss:10
-- comment: insert stackoverflow_links table values
insert into stackoverflow_links(link_id, answer_count) values (2, 3);









