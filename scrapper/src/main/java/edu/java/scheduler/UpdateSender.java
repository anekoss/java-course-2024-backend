package edu.java.scheduler;

import edu.java.client.dto.LinkUpdateRequest;

public interface UpdateSender {

    void send(LinkUpdateRequest updatedLink);
}
