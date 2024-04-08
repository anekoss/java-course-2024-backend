package edu.java.scheduler.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.domain.Link;
import edu.java.scheduler.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {
    private final GitHubClient gitHubClient;

    public Link check(Link link) {
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            Optional<GitHubResponse> gitHubResponse = gitHubClient.fetchRepository(githubValues[0], githubValues[1]);
            if (gitHubResponse.isPresent() && gitHubResponse.get().updatedAt() != null) {
                OffsetDateTime updatedAt = gitHubResponse.get().updatedAt();
                if (updatedAt.isAfter(link.getUpdatedAt())) {
                    link.setUpdatedAt(updatedAt);
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
