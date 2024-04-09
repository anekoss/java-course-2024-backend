package edu.java.scheduler;

import edu.java.domain.LinkEntity;
import edu.java.scheduler.dto.LinkUpdate;

public interface UpdateChecker {

    LinkUpdate check(LinkEntity link);
}
