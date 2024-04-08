package edu.java.scheduler.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.domain.Link;
import edu.java.scheduler.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {
    private static final int PART_QUESTION = 4;
    private final StackOverflowClient stackOverflowClient;

    public Link check(Link link) {
        Long question = getQuestion(link.getUri().toString());
        if (question != -1) {
            Optional<StackOverflowResponse> response = stackOverflowClient.fetchQuestion(question);
            if (response.isPresent()) {
                OffsetDateTime updatedAt = link.getUpdatedAt();
                for (StackOverflowResponse.StackOverflowItem item : response.get().items()) {
                    if (item != null && item.updatedAt() != null && item.updatedAt().isAfter(updatedAt)) {
                        updatedAt = item.updatedAt();
                    }
                }
                link.setUpdatedAt(updatedAt);
                link.setCheckedAt(OffsetDateTime.now());
            }
        }
        return link;
    }

    private Long getQuestion(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length >= PART_QUESTION) {
            return Long.parseLong(pathParts[PART_QUESTION]);
        }
        return -1L;
    }

}
