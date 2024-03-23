package edu.java.service.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import edu.java.repository.LinkRepository;
import edu.java.service.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.AbstractMap;
import java.util.Map;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {
    private static final int PART_QUESTION = 4;
    private final LinkRepository linkRepository;
    private final StackOverflowClient stackOverflowClient;

    public Map.Entry<Link, UpdateType> check(Link link) {
        UpdateType updateType = UpdateType.NO_UPDATE;
        Long question = getQuestion(link.getUri().toString());
        if (question != -1) {
            try {
                StackOverflowResponse response = stackOverflowClient.fetchQuestion(question);
                if (response != null) {
                    Long countAnswer = linkRepository.findStackOverflowAnswerCountByLinkId(link.getId());
                    OffsetDateTime updatedAt = link.getUpdatedAt();
                    for (StackOverflowResponse.StackOverflowItem item : response.items()) {
                        if (item.updatedAt().isAfter(updatedAt)) {
                            updatedAt = item.updatedAt();
                            updateType = UpdateType.UPDATE;
                            if (item.countAnswer() > countAnswer) {
                                countAnswer = item.countAnswer();
                                updateType = UpdateType.NEW_ANSWER;
                            }
                        }
                    }
                    linkRepository.updateAnswerCountByLinkId(countAnswer, link.getId());
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
