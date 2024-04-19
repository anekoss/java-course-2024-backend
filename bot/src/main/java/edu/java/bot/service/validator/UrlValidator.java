package edu.java.bot.service.validator;

import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UrlValidator implements Validator {

    public Optional<URL> isValid(String url) {
        if (url == null) {
            return Optional.empty();
        }
        try {
            URI uri = new URI(url);
            URL linkUrl = uri.toURL();
            boolean hostValid = uri.getHost().equals("github.com") || uri.getHost().equals("stackoverflow.com");
            return hostValid ? Optional.of(linkUrl) : Optional.empty();
        } catch (URISyntaxException | MalformedURLException | IllegalArgumentException e) {
            return Optional.empty();
        }
    }
}
