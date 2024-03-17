package edu.java.bot.domain;

import jakarta.validation.constraints.NotNull;
import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class UserRepository {
    private final Map<Long, User> userMap;
    private final Map<Long, Set<URL>> userLinksMap;

    public Optional<Long> addUser(@NotNull Long id, @NotNull User user) {
        if (userMap.putIfAbsent(id, user) == null) {
            userLinksMap.put(id, new HashSet<>());
            return Optional.of(id);
        }
        return Optional.empty();
    }

    public Optional<Set<URL>> removeUser(Long id) {
        User user = userMap.remove(id);
        if (user != null) {
            Set<URL> urls = userLinksMap.remove(id);
            return Optional.of(urls);
        }
        return Optional.empty();
    }

    public Optional<URL> addLinkToUser(Long id, URL url) {
        Set<URL> urls = userLinksMap.get(id);
        if (urls == null) {
            urls = new HashSet<>();
        }
        if (urls.contains(url)) {
            return Optional.empty();
        }
        urls.add(url);
        userLinksMap.put(id, urls);
        return Optional.of(url);
    }

    public Optional<URL> removeLinkFromUser(Long id, URL url) {
        Set<URL> urls = userLinksMap.get(id);
        boolean res = urls.remove(url);
        userLinksMap.put(id, urls);
        return !res ? Optional.empty() : Optional.of(url);
    }

    public Optional<Set<URL>> getUserLinks(Long id) {
        if (userMap.containsKey(id)) {
            return Optional.of(userLinksMap.get(id));
        }
        return Optional.empty();
    }

}
