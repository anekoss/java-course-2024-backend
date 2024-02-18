package edu.java.bot.db;

import java.net.URL;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class LinkRepository {
    private final Map<URL, Link> urlLinkMap;
    private final Map<URL, Set<Long>> linkUsersMap;

    @Autowired public LinkRepository(Map<URL, Link> urlLinkMap, Map<URL, Set<Long>> linkUsersMap) {
        this.urlLinkMap = urlLinkMap;
        this.linkUsersMap = linkUsersMap;
    }

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
