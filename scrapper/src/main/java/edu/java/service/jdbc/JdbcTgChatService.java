package edu.java.service.jdbc;

import edu.java.repository.jdbc.JdbcTgChatRepository;
import edu.java.service.service.AbstractTgChatService;

public class JdbcTgChatService extends AbstractTgChatService {

    public JdbcTgChatService(JdbcTgChatRepository tgChatRepository) {
        super(tgChatRepository);
    }

}
