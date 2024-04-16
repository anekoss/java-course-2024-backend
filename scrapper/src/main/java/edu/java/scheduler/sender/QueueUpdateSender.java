package edu.java.scheduler.sender;

import edu.java.client.dto.LinkUpdateRequest;
import edu.java.queue.ScrapperQueueProducer;
import edu.java.scheduler.UpdateSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class QueueUpdateSender implements UpdateSender {
    private final ScrapperQueueProducer queueProducer;

    @Override
    public void send(LinkUpdateRequest updatedLink) {
        queueProducer.send(updatedLink);
    }
}
