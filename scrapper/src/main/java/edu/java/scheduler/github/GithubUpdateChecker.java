package edu.java.scheduler.github;

import edu.java.domain.LinkEntity;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@AllArgsConstructor
public class GithubUpdateChecker implements UpdateChecker {
    private static final int PART_OWNER = 3;
    private static final int PART_REPOS = 4;
    private final GithubResponseHandler responseHandler;

    public LinkUpdate check(@NotNull LinkEntity link) {
        String[] githubValues = getOwnerAndReposGithub(link.getUri().toString());
        if (githubValues.length == 2) {
            String owner = githubValues[0];
            String repos = githubValues[1];
            return responseHandler.handle(owner, repos, link);
        }
        return new LinkUpdate(link, UpdateType.NO_UPDATE);
    }

    private String[] getOwnerAndReposGithub(@NotNull String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length > PART_REPOS) {
            return new String[] {pathParts[PART_OWNER], pathParts[PART_REPOS]};
        }
        return new String[] {};
    }
}
