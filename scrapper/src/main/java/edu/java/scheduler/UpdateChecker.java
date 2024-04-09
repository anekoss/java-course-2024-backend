package edu.java.scheduler;

import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.LinkEntity;

public interface UpdateChecker {

    LinkEntity check(LinkEntity link) throws CustomWebClientException;
}
