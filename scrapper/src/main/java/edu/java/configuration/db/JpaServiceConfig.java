package edu.java.configuration.db;

import edu.java.repository.jpa.JpaGithubLinkRepository;
import edu.java.repository.jpa.JpaLinkRepository;
import edu.java.repository.jpa.JpaStackOverflowLinkRepository;
import edu.java.repository.jpa.JpaTgChatRepository;
import edu.java.service.LinkService;
import edu.java.service.TgChatService;
import edu.java.service.jpa.JpaLinkService;
import edu.java.service.jpa.JpaTgChatService;
import edu.java.service.util.LinkTypeService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
@ConditionalOnProperty(prefix = "app", name = "db-access", havingValue = "jpa")
public class JpaServiceConfig {

    @Bean
    public TgChatService tgChatService(JpaTgChatRepository tgChatRepository) {
        return new JpaTgChatService(tgChatRepository);
    }

    @Bean
    LinkService linkService(
        JpaTgChatRepository tgChatRepository,
        JpaLinkRepository linkRepository,
        LinkTypeService linkTypeService,
        JpaStackOverflowLinkRepository stackOverflowLinkRepository,
        JpaGithubLinkRepository githubLinkRepository
    ) {
        return new JpaLinkService(
            tgChatRepository,
            linkRepository,
            linkTypeService,
            stackOverflowLinkRepository,
            githubLinkRepository
        );
    }

}
