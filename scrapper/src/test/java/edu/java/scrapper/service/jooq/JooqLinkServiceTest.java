package edu.java.scrapper.service.jooq;

import edu.java.repository.jooq.JooqChatLinkRepository;
import edu.java.repository.jooq.JooqGithubLinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqStackOverflowLinkRepository;
import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.scrapper.repository.jooq.JooqChatLinkRepositoryTest;
import edu.java.scrapper.service.service.LinkServiceTest;
import edu.java.service.jooq.JooqLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JooqLinkServiceTest extends LinkServiceTest {

    public JooqLinkServiceTest(
        @Autowired JooqLinkRepository linkRepository,
        @Autowired JooqChatLinkRepository chatLinkRepository,
        @Autowired Jooq tgChatRepository,
        @Autowired JooqStackOverflowLinkRepository stackOverflowLinkRepository,
        @Autowired JooqGithubLinkRepository githubLinkRepository,
        @Autowired JooqLinkService linkService
    ) {
        super(
            linkRepository,
            chatLinkRepository,
            tgChatRepository,
            stackOverflowLinkRepository,
            githubLinkRepository,
            linkService
        );
    }
}
