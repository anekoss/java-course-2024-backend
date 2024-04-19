package edu.java.scrapper.service.jdbc;

import edu.java.ScrapperApplication;
import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.service.service.LinkServiceTest;
import edu.java.service.jdbc.JdbcLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jdbc"})
public class JdbcLinkServiceTest extends LinkServiceTest {

    public JdbcLinkServiceTest(
        @Autowired JdbcLinkRepository linkRepository,
        @Autowired JdbcChatLinkRepository chatLinkRepository,
        @Autowired JdbcTgChatRepository tgChatRepository,
        @Autowired JdbcLinkService linkService
    ) {
        super(
            linkRepository,
            chatLinkRepository,
            tgChatRepository,
            linkService
        );
    }
}
