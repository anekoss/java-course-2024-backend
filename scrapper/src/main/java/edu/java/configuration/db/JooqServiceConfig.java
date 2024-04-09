package edu.java.configuration.db;

import edu.java.repository.jooq.JooqGithubLinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqStackOverflowLinkRepository;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.jooq.JooqTgChatService;
import edu.java.service.util.LinkTypeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "db-access", havingValue = "jooq")
public class JooqServiceConfig {

    @Bean
    public TgChatService tgChatService(JooqTgChatRepository tgChatRepository) {
        return new JooqTgChatService(tgChatRepository);
    }

    @Bean LinkService linkService(
        JooqTgChatRepository tgChatRepository,
        JooqLinkRepository linkRepository,
        LinkTypeService linkTypeService,
        JooqChatLinkRepository chatLinkRepository,
        JooqStackOverflowLinkRepository stackOverflowLinkRepository,
        JooqGithubLinkRepository githubLinkRepository
    ) {
        return new JooqLinkService(
            tgChatRepository,
            linkRepository,
            linkTypeService,
            chatLinkRepository,
            stackOverflowLinkRepository,
            githubLinkRepository
        );
    }

}
