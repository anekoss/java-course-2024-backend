package edu.java.service.updateChecker;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.client.exception.BadResponseBodyException;
import edu.java.domain.Link;
import edu.java.service.UpdateChecker;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {
    private final StackOverflowClient stackOverflowClient;

    public Link check(Link link) {
        Long question = getQuestion(link.getUri().toString());
        if (question != -1) {
            try {
                StackOverflowResponse response = stackOverflowClient.fetchQuestion(question);
                if (response != null) {
                    OffsetDateTime updatedAt = link.getUpdatedAt();
                    for (StackOverflowResponse.StackOverflowItem item : response.items()) {
                        if (item.updatedAt().isAfter(updatedAt)) {
                            updatedAt = item.updatedAt();
                        }
                    }
                    link.setUpdatedAt(updatedAt);
                    link.setUpdatedAt(OffsetDateTime.now());
                }
            } catch (BadResponseBodyException e) {
                log.error(e.getMessage());
            }
        }
        return link;
    }

    private Long getQuestion(String uri) {
        Pattern pattern = Pattern.compile("/(\\d+)/");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Long.parseLong(matcher.group());
        }
        return -1L;
    }

}
