package edu.java.bot.service;

import edu.java.bot.service.validator.UrlValidator;
import edu.java.bot.service.validator.Validator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.net.URL;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

public class UrlValidatorTest {

    public static Stream<Arguments> provideDataForTest() throws URISyntaxException, MalformedURLException {
        return Stream.of(
            Arguments.of("https://github.com/anekoss/tinkoff-project", Optional.of(new URI("https://github.com/anekoss/tinkoff-project").toURL())),
            Arguments.of("invalid", Optional.empty()),
            Arguments.of(null, Optional.empty()),
            Arguments.of("", Optional.empty()),
            Arguments.of("https://www.example.com", Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void testValidUrl(String url, Optional<URL> excepted) {
        Validator validator = new UrlValidator();
        assertThat(validator.isValid(url)).isEqualTo(excepted);
    }
}
