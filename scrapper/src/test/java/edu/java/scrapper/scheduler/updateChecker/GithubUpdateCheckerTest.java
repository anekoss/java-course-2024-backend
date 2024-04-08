package edu.java.scrapper.scheduler.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.updateChecker.GithubUpdateChecker;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;

import java.net.URI;
import java.time.OffsetDateTime;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;

public class GithubUpdateCheckerTest {
    private final GitHubClient gitHubClient = Mockito.mock(GitHubClient.class);
    private final GithubUpdateChecker updateChecker = new GithubUpdateChecker(gitHubClient);
    private final GitHubResponse response = new GitHubResponse(
            755879115L,
            "tinkoff-project",
            "anekoss/tinkoff-project",
            OffsetDateTime.parse("2024-02-11T11:13:17Z"),
            OffsetDateTime.parse("2024-02-21T12:54:35Z"),
            OffsetDateTime.parse("2024-02-11T11:13:57Z")
    );
    private final Link link = new Link().setUri(URI.create("https://github.com/anekoss/tinkoff-project"))
                                        .setLinkType(LinkType.GITHUB);

    @Test
    void testCheck_shouldCorrectlyUpdateLink() throws CustomWebClientException {
        OffsetDateTime checkedAt = OffsetDateTime.parse("2023-02-11T11:13:57Z");
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenReturn(response);
        link.setCheckedAt(checkedAt).setUpdatedAt(checkedAt);
        Link updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getUpdatedAt()).isEqualToIgnoringNanos(response.updatedAt());
        assertThat(updatedLink.getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheck_shouldUpdateCheckAtIfNoUpdate() throws CustomWebClientException {
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenReturn(response);
        OffsetDateTime checkedAt = OffsetDateTime.now().minusDays(1);
        OffsetDateTime updatedAt = response.updatedAt();
        link.setCheckedAt(checkedAt).setUpdatedAt(updatedAt);
        Link updatedLink = updateChecker.check(link);
        assertThat(updatedLink.getUpdatedAt()).isEqualToIgnoringNanos(updatedAt);
        assertThat(updatedLink.getCheckedAt()).isAfter(checkedAt);
    }

    @Test
    void testCheck_shouldThrowExceptionIfClientError() throws CustomWebClientException {
        when(gitHubClient.fetchRepository(anyString(), anyString())).thenThrow(CustomWebClientException.class);
        assertThrows(CustomWebClientException.class, () -> updateChecker.check(link));
    }

}
