package edu.java.scrapper.service;

import edu.java.service.LinkTypeService;
import lombok.RequiredArgsConstructor;
import org.junit.jupiter.api.Test;
import static edu.java.domain.LinkType.GITHUB;
import static edu.java.domain.LinkType.STACKOVERFLOW;
import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.Assert.assertThrows;

@RequiredArgsConstructor
public class LinkTypeServiceTest {
    private final LinkTypeService linkTypeService;

    @Test
    void testShouldReturnCorrectType() {
        assertThat(linkTypeService.getType("stackoverflow.com")).isEqualTo(STACKOVERFLOW);
        assertThat(linkTypeService.getType("github.com")).isEqualTo(GITHUB);
    }

    @Test
    void testShouldThrowException() {
        assertThrows(IllegalArgumentException.class, () -> linkTypeService.getType("edu.tinkoff.ru"));
    }
}
