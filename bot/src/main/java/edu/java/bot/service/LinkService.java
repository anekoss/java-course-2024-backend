package edu.java.bot.service;

import edu.java.bot.db.Link;
import edu.java.bot.service.validator.Validator;
import java.net.URL;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class LinkService {
    private final Validator urlValidator;

    public Optional<Link> createValidLink(String stringUrl) {
        Optional<URL> url = urlValidator.isValid(stringUrl);
        return url.map(Link::new);
    }

}
