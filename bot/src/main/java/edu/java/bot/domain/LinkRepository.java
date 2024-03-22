package edu.java.bot.domain;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@RequiredArgsConstructor
public class LinkRepository {
    private final Map<URL, Link> urlLinkMap;
    private final Map<URL, Set<Long>> linkUsersMap;

    public void addUserToLink(Long id, URL url, Link link) {
        urlLinkMap.putIfAbsent(url, link);
        Set<Long> users = linkUsersMap.get(url);
        if (users == null) {
            users = new HashSet<>();
        }
        users.add(id);
        linkUsersMap.put(url, users);
    }

    public void removeUserFromLink(Long id, URL url) {
        Set<Long> users = linkUsersMap.get(url);
        if (users != null) {
            users.remove(id);
            if (users.isEmpty()) {
                linkUsersMap.remove(url);
                urlLinkMap.remove(url);
            } else {
                linkUsersMap.put(url, users);
            }
        }
    }

    public void removeUserFromLink(Long id, Set<URL> urls) {
        urls.forEach(url -> removeUserFromLink(id, url));
    }

}
