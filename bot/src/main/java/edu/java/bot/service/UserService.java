package edu.java.bot.service;

import edu.java.bot.db.User;
import org.springframework.stereotype.Service;

@Service
public class UserService {

    public UserService() {
    }

    public User createUser(Long id) {
        return new User(id);
    }
}
