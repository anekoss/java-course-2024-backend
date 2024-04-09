package edu.java.scheduler.stackoverflow;

import edu.java.domain.LinkEntity;
import edu.java.scheduler.dto.LinkUpdate;

public interface StackOverflowResponseHandler {

    LinkUpdate handle(long question, LinkEntity link);
}
