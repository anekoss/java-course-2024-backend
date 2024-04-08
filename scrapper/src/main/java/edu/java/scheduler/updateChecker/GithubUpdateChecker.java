package edu.java.scheduler.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.Link;
import edu.java.scheduler.UpdateChecker;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {
    private final GitHubClient gitHubClient;

    public Link check(Link link) throws CustomWebClientException {
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            GitHubResponse gitHubResponse = gitHubClient.fetchRepository(githubValues[0], githubValues[1]);
            if (gitHubResponse != null && gitHubResponse.updatedAt() != null) {
                if (gitHubResponse.updatedAt().isAfter(link.getUpdatedAt())) {
                    link.setUpdatedAt(gitHubResponse.updatedAt());
                }
                link.setCheckedAt(OffsetDateTime.now());
            }
        }
        return link;
    }

    private String[] getOwnerAndReposGithub(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length > 2) {
            return new String[] {pathParts[1], pathParts[2]};
        }
        return new String[0];
    }
}
