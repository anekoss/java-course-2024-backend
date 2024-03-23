package edu.java.scrapper.service.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.domain.UpdateType;
import edu.java.service.updateChecker.GithubUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.Map;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static edu.java.domain.UpdateType.NO_UPDATE;
import static edu.java.domain.UpdateType.UPDATE;
import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GithubUpdateCheckerTest {
    private final GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
    private final GithubUpdateChecker updateChecker = new GithubUpdateChecker(gitHubClient);

    @Test
    void testUpdateShouldUpdate() throws BadResponseBodyException {
        GitHubResponse response = new GitHubResponse(
            755879115L,
            "tinkoff-project",
            "anekoss/tinkoff-project",
            OffsetDateTime.parse("2024-02-11T11:13:17Z"),
            OffsetDateTime.parse("2024-02-21T12:54:35Z"),
            OffsetDateTime.parse("2024-02-11T11:13:57Z")
        );
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenReturn(response);
        Link link = new Link(URI.create("https://github.com/anekoss/tinkoff-project"), LinkType.GITHUB);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(OffsetDateTime.parse("2024-02-11T11:13:57Z"));
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testUpdateShouldNotUpdate() throws BadResponseBodyException {
        GitHubResponse response = new GitHubResponse(
            755879115L,
            "tinkoff-project",
            "anekoss/tinkoff-project",
            OffsetDateTime.parse("2024-02-11T11:13:17Z"),
            OffsetDateTime.parse("2024-02-21T12:54:35Z"),
            OffsetDateTime.parse("2022-02-11T11:13:57Z")
        );
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenReturn(response);
        Link link = new Link(URI.create("https://github.com/anekoss/tinkoff-project"), LinkType.GITHUB);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testUpdateShouldReturnInputLink() throws BadResponseBodyException {
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenThrow(BadResponseBodyException.class);
        Link link = new Link(URI.create("https://github.com/anekoss/tinkoff-project"), LinkType.GITHUB);
        OffsetDateTime checkedAt = OffsetDateTime.now();
        link.setUpdatedAt(OffsetDateTime.parse("2023-02-11T11:13:57Z"));
        link.setCheckedAt(checkedAt);
        Map.Entry<Link, UpdateType> updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getValue()).isEqualTo(NO_UPDATE);
        assertThat(updatedLink.getKey().getUpdatedAt()).isEqualTo(link.getUpdatedAt());
        assertThat(updatedLink.getKey().getCheckedAt()).isEqualTo(link.getCheckedAt());
    }

}
