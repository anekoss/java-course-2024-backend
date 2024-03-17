package edu.java.repository;

import edu.java.domain.Link;
import java.net.URI;
import java.util.List;

public interface LinkRepository {

    int save(Link link);

    int delete(Link link);

    Link findByUri(URI uri);

    List<Link> findAll();

    List<Link> findStaleLinks(long limit);
}
