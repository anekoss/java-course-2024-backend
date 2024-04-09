package edu.java.scrapper.service.jooq;

import edu.java.ScrapperApplication;
import edu.java.repository.jooq.JooqChatLinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.scrapper.service.service.LinkServiceTest;
import edu.java.service.jooq.JooqLinkService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(classes = ScrapperApplication.class, properties = {"app.db-access=jooq"})
public class JooqLinkServiceTest extends LinkServiceTest {

    public JooqLinkServiceTest(
        @Autowired JooqLinkRepository linkRepository,
        @Autowired JooqChatLinkRepository chatLinkRepository,
        @Autowired JooqTgChatRepository tgChatRepository,
        @Autowired JooqLinkService linkService
    ) {
        super(
            linkRepository,
            chatLinkRepository,
            tgChatRepository,
            linkService
        );
    }
}
