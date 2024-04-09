package edu.java.repository.jpa;

import edu.java.domain.Link;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public interface JpaLinkRepository extends JpaRepository<Link, Long> {
    Optional<Link> findByUri(URI uri);

    @Query(value = "select from links order by checked_at asc limit :limit", nativeQuery = true)
    List<Link> findStaleLinks(
            @Param("limit") long limit);

    @Modifying
    @Query(value = "update links set updated_at = :updatedAt, checked_at = :checkedAt where id = :id", nativeQuery = true)
    Long updateById(
            @Param("id") Long id,
            @Param("checkedAt") OffsetDateTime checkedAt,
            @Param("updatedAt") OffsetDateTime updatedAt);

}
