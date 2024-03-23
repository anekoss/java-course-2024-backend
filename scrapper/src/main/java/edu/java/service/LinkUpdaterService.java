package edu.java.service;

import edu.java.domain.Link;
import edu.java.domain.UpdateType;
import java.util.Map;

public interface LinkUpdaterService {
    Map<Link, UpdateType>  update();

    long sendUpdates(Map<Link, UpdateType> updateTypeMap);
}
