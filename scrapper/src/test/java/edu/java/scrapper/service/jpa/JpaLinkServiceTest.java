package edu.java.scrapper.service.jpa;

import edu.java.ScrapperApplication;
import edu.java.repository.ChatLinkRepository;
import edu.java.repository.LinkRepository;
import edu.java.repository.TgChatRepository;
import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.scrapper.service.service.LinkServiceTest;
import edu.java.service.jpa.JpaLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jpa"})
public class JpaLinkServiceTest extends LinkServiceTest {
    public JpaLinkServiceTest(
        @Autowired JdbcLinkRepository linkRepository,
        @Autowired JdbcChatLinkRepository chatLinkRepository,
        @Autowired JdbcTgChatRepository tgChatRepository,
        @Autowired JpaLinkService linkService
    ) {
        super(linkRepository, chatLinkRepository, tgChatRepository, linkService);
    }
}
