package edu.java.repository;

import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.LinkEntity;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {

    long add(LinkEntity linkEntity);

    long remove(URI uri) throws LinkNotFoundException;

    List<LinkEntity> findAll();

    Optional<LinkEntity> findByUri(URI uri);

    Optional<LinkEntity> findById(long id);

    List<LinkEntity> findStaleLinks(Long limit);

    long update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt);

}
