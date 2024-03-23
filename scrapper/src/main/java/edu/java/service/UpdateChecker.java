package edu.java.service;

import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import java.util.Map;

public interface UpdateChecker {

    Map.Entry<Link, UpdateType> check(Link link);
}
