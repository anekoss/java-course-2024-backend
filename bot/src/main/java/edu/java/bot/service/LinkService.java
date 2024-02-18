package edu.java.bot.service;

import edu.java.bot.db.Link;
import edu.java.bot.service.validator.Validator;
import java.net.URL;
import java.util.Optional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class LinkService {
    private final Validator urlValidator;

    @Autowired
    public LinkService(Validator urlValidator) {
        this.urlValidator = urlValidator;
    }

    public Optional<Link> createValidLink(String stringUrl) {
        Optional<URL> url = urlValidator.isValid(stringUrl);
        return url.map(Link::new);
    }

}
