package edu.java.service;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.domain.Link;
import java.time.OffsetDateTime;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Component;

@Component
@AllArgsConstructor
public class GithubUpdater {
    private final GitHubClient gitHubClient;

    public Link update(Link link) {
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            GitHubResponse gitHubResponse = gitHubClient.fetchRepository(githubValues[0], githubValues[1]);
            if (gitHubResponse != null) {
                OffsetDateTime updatedAt = link.getUpdatedAt();
                if (gitHubResponse.updatedAt().isAfter(updatedAt)) {
                    updatedAt = gitHubResponse.updatedAt();
                }
                link.setUpdatedAt(updatedAt);
            }
        }
        return link;
    }

    private String[] getOwnerAndReposGithub(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length >= 3) {
            String[] paths = new String[2];
            paths[0] = pathParts[1];
            paths[1] = pathParts[2];
            return paths;
        }
        return new String[0];
    }
}
