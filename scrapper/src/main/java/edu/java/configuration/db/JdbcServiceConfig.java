package edu.java.configuration.db;

import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcGithubLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcStackOverflowLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import edu.java.service.jdbc.JdbcLinkService;
import edu.java.service.jdbc.JdbcTgChatService;
import edu.java.service.jooq.JooqLinkService;
import edu.java.service.util.LinkTypeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "db-access", havingValue = "jdbc")
public class JdbcServiceConfig {

    @Bean
    public TgChatService tgChatService(JdbcTgChatRepository tgChatRepository) {
        return new JdbcTgChatService(tgChatRepository);
    }

    @Bean LinkService linkService(
        JdbcTgChatRepository tgChatRepository,
        JdbcLinkRepository linkRepository,
        LinkTypeService linkTypeService,
        JdbcChatLinkRepository chatLinkRepository,
        JdbcStackOverflowLinkRepository stackOverflowLinkRepository,
        JdbcGithubLinkRepository githubLinkRepository
    ) {
        return new JdbcLinkService(
            tgChatRepository,
            linkRepository,
            linkTypeService,
            chatLinkRepository,
            stackOverflowLinkRepository,
            githubLinkRepository
        );
    }

}
