package edu.java.service.updateChecker;

import edu.java.client.GitHubClient;
import edu.java.client.dto.GitHubBranchResponse;
import edu.java.client.dto.GitHubResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import edu.java.repository.GithubLinkRepository;
import edu.java.service.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import static edu.java.domain.UpdateType.NEW_BRANCH;
import static edu.java.domain.UpdateType.NO_UPDATE;

@Slf4j
@Service
@AllArgsConstructor
public class JdbcGithubUpdateChecker implements UpdateChecker {
    private final GitHubClient gitHubClient;
    private final GithubLinkRepository linkRepository;

    public Map.Entry<Link, UpdateType> check(Link link) {
        UpdateType updateType = NO_UPDATE;
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
        if (updateType == UpdateType.UPDATE) {
            updateType = checkBranches(githubValues, link, updateType);
        }
        return new AbstractMap.SimpleEntry<>(link, updateType);
    }

    public UpdateType checkBranches(String[] githubValues, Link link, UpdateType type) {
        Optional<Long> optionalCount = linkRepository.findGithubBranchCountByLinkId(link.getId());
        Long count = optionalCount.orElse(0L);
        try {
            GitHubBranchResponse[] response = gitHubClient.fetchRepositoryBranches(githubValues[0], githubValues[1]);
            if (response != null && response != null) {
                if (response.length != count) {
                    count = (long) response.length;
                }
            }
            if (optionalCount.isEmpty()) {
                linkRepository.add(link.getId(), count);
            } else if (!count.equals(optionalCount.get())) {
                linkRepository.update(link.getId(), count);
                return NEW_BRANCH;
            }
        } catch (BadResponseBodyException e) {
            log.info(e.getMessage());
        }
        return type;
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
