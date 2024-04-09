package edu.java.scheduler;

import edu.java.client.exception.CustomWebClientException;
import edu.java.domain.Link;

public interface UpdateChecker {

    Link check(Link link) throws CustomWebClientException;
}
