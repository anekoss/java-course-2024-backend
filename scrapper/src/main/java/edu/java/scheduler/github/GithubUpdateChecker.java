package edu.java.scheduler.github;

import edu.java.domain.Link;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.handler.GithubRepositoryResponseHandler;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {
    private final GithubRepositoryResponseHandler repositoryResponseService;

    public LinkUpdate check(@NotNull Link link) {
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            String owner = githubValues[0];
            String repos = githubValues[1];
            return repositoryResponseService.handle(owner, repos, link);
        }
        return new LinkUpdate(link, UpdateType.NO_UPDATE);
    }

    private String[] getOwnerAndReposGithub(@NotNull String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length > 2) {
            return new String[] {pathParts[1], pathParts[2]};
        }
        return new String[] {};
    }
}
