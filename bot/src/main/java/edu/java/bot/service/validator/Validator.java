package edu.java.bot.service.validator;

import java.net.URL;
import java.util.Optional;

public interface Validator {
    Optional<URL> isValid(String url);
}
