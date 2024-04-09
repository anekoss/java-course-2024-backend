package edu.java.scheduler;

import edu.java.domain.Link;
import edu.java.scheduler.dto.LinkUpdate;

public interface UpdateChecker {

    LinkUpdate check(Link link);
}
