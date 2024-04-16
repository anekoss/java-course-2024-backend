package edu.java.scheduler.sender;

import edu.java.client.BotClient;
import edu.java.client.dto.LinkUpdateRequest;
import edu.java.scheduler.UpdateSender;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class HttpUpdateSender implements UpdateSender {
    private final BotClient botClient;

    @Override
    public void send(LinkUpdateRequest updatedLink) {
        botClient.linkUpdates(updatedLink);
    }
}
