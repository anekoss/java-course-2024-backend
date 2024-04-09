package edu.java.service.jooq;

import edu.java.repository.jooq.JooqChatLinkRepository;
import edu.java.repository.jooq.JooqGithubLinkRepository;
import edu.java.repository.jooq.JooqLinkRepository;
import edu.java.repository.jooq.JooqStackOverflowLinkRepository;
import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.service.service.AbstractLinkService;
import edu.java.service.util.LinkTypeService;

public class JooqLinkService extends AbstractLinkService {
    public JooqLinkService(
        JooqTgChatRepository tgChatRepository,
        JooqLinkRepository linkRepository,
        LinkTypeService linkTypeService,
        JooqChatLinkRepository chatLinkRepository,
        JooqStackOverflowLinkRepository stackOverflowLinkRepository,
        JooqGithubLinkRepository githubLinkRepository
    ) {
        super(
            tgChatRepository,
            linkRepository,
            linkTypeService,
            chatLinkRepository,
            stackOverflowLinkRepository,
            githubLinkRepository
        );
    }
}
