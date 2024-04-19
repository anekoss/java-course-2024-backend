package edu.java.service.jdbc;

import edu.java.repository.jdbc.JdbcChatLinkRepository;
import edu.java.repository.jdbc.JdbcGithubLinkRepository;
import edu.java.repository.jdbc.JdbcLinkRepository;
import edu.java.repository.jdbc.JdbcStackOverflowLinkRepository;
import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.service.service.AbstractLinkService;
import edu.java.service.util.LinkTypeService;

public class JdbcLinkService extends AbstractLinkService {

    public JdbcLinkService(
        JdbcTgChatRepository tgChatRepository,
        JdbcLinkRepository linkRepository,
        LinkTypeService linkTypeService,
        JdbcChatLinkRepository chatLinkRepository,
        JdbcStackOverflowLinkRepository stackOverflowLinkRepository,
        JdbcGithubLinkRepository githubLinkRepository
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
