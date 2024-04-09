package edu.java.service.jooq;

import edu.java.repository.jooq.JooqTgChatRepository;
import edu.java.service.service.AbstractTgChatService;

public class JooqTgChatService extends AbstractTgChatService {
    public JooqTgChatService(JooqTgChatRepository jooqTgChatRepository) {
        super(jooqTgChatRepository);
    }
}
