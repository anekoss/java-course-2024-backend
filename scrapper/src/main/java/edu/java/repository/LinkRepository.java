package edu.java.repository;

import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.Link;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

public interface LinkRepository {

    long add(Link link);

    long remove(URI uri) throws LinkNotFoundException;

    List<Link> findAll();

    Optional<Link> findByUri(URI uri);

    Optional<Link> findById(long id);

    List<Link> findStaleLinks(Long limit);

    long update(Long linkId, OffsetDateTime updatedAt, OffsetDateTime checkedAt);

}
