package edu.java.scrapper.service.jdbc;

import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcGithubLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcStackOverflowLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.service.service.LinkServiceTest;
import edu.java.service.jdbc.JdbcLinkService;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest
public class JdbcLinkServiceTest extends LinkServiceTest {

    public JdbcLinkServiceTest(
        JdbcLinkRepository linkRepository,
        JdbcChatLinkRepository chatLinkRepository,
        JdbcTgChatRepository tgChatRepository,
        JdbcStackOverflowLinkRepository stackOverflowLinkRepository,
        JdbcGithubLinkRepository githubLinkRepository,
        JdbcLinkService linkService
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
