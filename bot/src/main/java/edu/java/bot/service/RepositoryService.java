package edu.java.bot.service;

import edu.java.bot.domain.Link;
import edu.java.bot.domain.LinkRepository;
import edu.java.bot.domain.User;
import edu.java.bot.domain.UserRepository;
import java.net.URL;
import java.util.Optional;
import java.util.Set;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class RepositoryService {
    private final LinkRepository linkRepository;
    private final UserRepository userRepository;

    public boolean saveUser(Long id, User user) {
        return userRepository.addUser(id, user).isPresent();
    }

    public boolean removeUser(Long id) {
        Optional<Set<URL>> urlsOptional = userRepository.removeUser(id);
        urlsOptional.ifPresent(urlSet -> linkRepository.removeUserFromLink(id, urlSet));
        return urlsOptional.isPresent();
    }

    public boolean startTrackLink(Long id, URL url, Link link) {
        Optional<URL> urlOptional = userRepository.addLinkToUser(id, url);
        if (urlOptional.isPresent()) {
            linkRepository.addUserToLink(id, url, link);
        }
        return urlOptional.isPresent();
    }

    public boolean stopTrackLink(Long id, URL url) {
        Optional<URL> urlOptional = userRepository.removeLinkFromUser(id, url);
        if (urlOptional.isPresent()) {
            linkRepository.removeUserFromLink(id, url);
        }
        return urlOptional.isPresent();
    }

    public Set<URL> getTrackLinks(Long id) {
        Optional<Set<URL>> urlsOptional = userRepository.getUserLinks(id);
        return urlsOptional.orElseGet(Set::of);
    }

}
