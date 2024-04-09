package edu.java.scheduler.stackoverflow;

import edu.java.domain.Link;
import edu.java.scheduler.dto.LinkUpdate;

public interface StackOverflowResponseHandler {

    LinkUpdate handle(long question, Link link);
}
