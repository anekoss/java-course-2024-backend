package edu.java.service.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import edu.java.service.UpdateChecker;

import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Map;

import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {
    private final GitHubClient gitHubClient;

    public Map.Entry<Link, UpdateType> check(Link link) {
        UpdateType updateType = UpdateType.NO_UPDATE;
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            try {
                GitHubResponse gitHubResponse = gitHubClient.fetchRepository(githubValues[0], githubValues[1]);
                if (gitHubResponse != null && gitHubResponse.updatedAt() != null
                    && gitHubResponse.updatedAt().isAfter(link.getUpdatedAt())) {
                    link.setUpdatedAt(gitHubResponse.updatedAt());
                    updateType = UpdateType.UPDATE;
                }
                link.setCheckedAt(OffsetDateTime.now());
            } catch (BadResponseBodyException e) {
                log.error(e.getMessage());
            }
        }
        return new AbstractMap.SimpleEntry<>(link, updateType);
    }

    private String[] getOwnerAndReposGithub(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length > 2) {
            String[] paths = new String[2];
            paths[0] = pathParts[1];
            paths[1] = pathParts[2];
            return paths;
        }
        return new String[0];
    }
}
