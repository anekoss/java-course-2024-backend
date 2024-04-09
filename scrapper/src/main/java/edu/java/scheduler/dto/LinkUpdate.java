package edu.java.scheduler.dto;

import edu.java.domain.Link;

public record LinkUpdate(Link link, UpdateType type) {
}
