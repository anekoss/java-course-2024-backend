package edu.java.configuration;

import edu.java.domain.LinkType;
import edu.java.service.UpdateChecker;
import edu.java.service.updateChecker.GithubUpdateChecker;
import edu.java.service.updateChecker.StackOverflowUpdateChecker;
import java.util.Map;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class JdbcConfig {
    private final Long limit = 10L;

    @Bean
    Map<LinkType, UpdateChecker> updateCheckerMap(
        StackOverflowUpdateChecker stackOverflowUpdateChecker,
        GithubUpdateChecker githubUpdateChecker
    ) {
        return Map.of(LinkType.STACKOVERFLOW, stackOverflowUpdateChecker, LinkType.GITHUB, githubUpdateChecker);
    }

    @Bean
    Long limit() {
        return limit;
    }
}
