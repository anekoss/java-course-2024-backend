package edu.java.service.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import edu.java.repository.StackOverflowLinkRepository;
import edu.java.service.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Map;
import java.util.Optional;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class JdbcStackOverflowUpdateChecker implements UpdateChecker {
    private static final int PART_QUESTION = 4;
    private final StackOverflowLinkRepository linkRepository;
    private final StackOverflowClient stackOverflowClient;

    public Map.Entry<Link, UpdateType> check(Link link) {
        UpdateType updateType = UpdateType.NO_UPDATE;
        Long question = getQuestion(link.getUri().toString());
        if (question != -1) {
            try {
                StackOverflowResponse response = stackOverflowClient.fetchQuestion(question);
                if (response != null) {
                    Optional<Long> optionalCount = linkRepository.findStackOverflowAnswerCountByLinkId(link.getId());
                    Long countAnswer = optionalCount.orElse(0L);
                    OffsetDateTime updatedAt = link.getUpdatedAt();
                    for (StackOverflowResponse.StackOverflowItem item : response.items()) {
                        if (item.updatedAt().isAfter(updatedAt)) {
                            updatedAt = item.updatedAt();
                            updateType = UpdateType.UPDATE;
                            if (item.countAnswer() > countAnswer) {
                                countAnswer = item.countAnswer();
                                if (optionalCount.isPresent()) {
                                    updateType = UpdateType.NEW_ANSWER;
                                }
                            }
                        }
                    }
                    linkRepository.update(link.getId(), countAnswer);
                    link.setUpdatedAt(updatedAt);
                    link.setCheckedAt(OffsetDateTime.now());
                }
            } catch (BadResponseBodyException e) {
                log.error(e.getMessage());
            }
        }
        return new AbstractMap.SimpleEntry<>(link, updateType);
    }

    private Long getQuestion(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length >= PART_QUESTION) {
            return Long.parseLong(pathParts[PART_QUESTION]);
        }
        return -1L;
    }

}
