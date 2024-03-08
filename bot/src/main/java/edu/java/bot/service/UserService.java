package edu.java.bot.service;

import edu.java.bot.db.User;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserService {

    public User createUser(Long id) {
        return new User(id);
    }
}
