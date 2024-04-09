package edu.java.scheduler.dto;

import edu.java.domain.LinkEntity;

public record LinkUpdate(LinkEntity link, UpdateType type) {
}
