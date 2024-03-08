package edu.java.bot.configuration;

import edu.java.bot.db.Link;
import edu.java.bot.db.User;
import java.net.URL;
import java.util.HashMap;
import java.util.Map;
import java.util.Set;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class DatabaseConfiguration {

    @Bean
    public Map<URL, Link> urlLinkMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, User> userMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<Long, Set<URL>> userLinksMap() {
        return new HashMap<>();
    }

    @Bean
    public Map<URL, Set<Long>> linkUsersMap() {
        return new HashMap<>();
    }
}
