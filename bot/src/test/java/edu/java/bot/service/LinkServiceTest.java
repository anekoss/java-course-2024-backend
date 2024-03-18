package edu.java.bot.service;

import edu.java.bot.domain.Link;
import edu.java.bot.service.validator.UrlValidator;
import java.net.MalformedURLException;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.Optional;
import java.util.stream.Stream;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.Arguments;
import org.junit.jupiter.params.provider.MethodSource;
import static org.assertj.core.api.Assertions.assertThat;

public class LinkServiceTest {

    public static Stream<Arguments> provideDataForTest() throws URISyntaxException, MalformedURLException {
        return Stream.of(
            Arguments.of("https://www.example.com", Optional.of(new Link(new URI("https://www.example.com").toURL()))),
            Arguments.of("invalid", Optional.empty()),
            Arguments.of(null, Optional.empty()),
            Arguments.of("", Optional.empty()),
            Arguments.of("https://www.example.com:8080:invalid", Optional.empty())
        );
    }

    @ParameterizedTest
    @MethodSource("provideDataForTest")
    void test(String url, Optional<Link> excepted) {
        LinkService linkService = new LinkService(new UrlValidator());
        assertThat(linkService.createValidLink(url)).isEqualTo(excepted);
    }
}
