package edu.java.bot.service.validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import org.springframework.stereotype.Service;

@Service
public class UrlValidator implements Validator {

    public UrlValidator() {
    }

    public Optional<URL> isValid(String url) {
        if (url == null) {
            return Optional.empty();
        }
        try {
            return Optional.of(new URI(url).toURL());
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
