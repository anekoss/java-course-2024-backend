package edu.java.scheduler.stackoverflow.handler;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.domain.Link;
import edu.java.domain.StackOverflowLink;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.stackoverflow.StackOverflowResponseHandler;
import edu.java.service.LinkService;
import java.time.OffsetDateTime;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@AllArgsConstructor
@Service
public class StackOverflowQuestionResponseHandler implements StackOverflowResponseHandler {
    private final StackOverflowClient stackOverflowClient;
    private final LinkService linkService;

    @Override
    public LinkUpdate handle(long question, Link link) {
        Optional<StackOverflowResponse> response = stackOverflowClient.fetchQuestion(question);
        if (response.isPresent()) {
            Optional<StackOverflowResponse.StackOverflowItem> opItem = response.get().items()
                                                                               .stream()
                                                                               .filter(item -> item != null
                                                                                   && item.updatedAt() != null
                                                                                   && item.updatedAt()
                                                                                          .isAfter(link.getUpdatedAt()))
                                                                               .findFirst();
            link.setCheckedAt(OffsetDateTime.now());
            if (opItem.isPresent()) {
                link.setUpdatedAt(opItem.get().updatedAt());
                StackOverflowLink stackOverflowLink =
                    new StackOverflowLink(link.getId(), opItem.get().countAnswer());
                UpdateType type = linkService.updateStackOverflowAnswerCount(stackOverflowLink);
                return type == UpdateType.UPDATE_ANSWER ? new LinkUpdate(link, type)
                    : new LinkUpdate(link, UpdateType.UPDATE);
            }
        }
        return new LinkUpdate(link, UpdateType.NO_UPDATE);
    }
}
