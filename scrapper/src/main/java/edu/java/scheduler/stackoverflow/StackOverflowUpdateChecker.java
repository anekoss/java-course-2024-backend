package edu.java.scheduler.stackoverflow;

import edu.java.domain.LinkEntity;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@AllArgsConstructor
public class StackOverflowUpdateChecker implements UpdateChecker {
    private static final int PART_QUESTION = 4;
    private final StackOverflowResponseHandler responseHandler;

    public LinkUpdate check(LinkEntity link) {
        Long question = getQuestion(link.getUri().toString());
        if (question != -1L) {
            return responseHandler.handle(question, link);
        }
        return new LinkUpdate(link, UpdateType.NO_UPDATE);
    }

    private Long getQuestion(String uri) {
        String[] pathParts = uri.split("/");
        if (pathParts.length > PART_QUESTION) {
            return Long.parseLong(pathParts[PART_QUESTION]);
        }
        return -1L;
    }

}
