package edu.java.repository.jpa;

import edu.java.domain.LinkEntity;
import java.net.URI;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;

@Repository
public interface JpaLinkRepository extends JpaRepository<LinkEntity, Long> {
    Optional<LinkEntity> findByUri(URI uri);

    @Query(value = "select * from links order by checked_at asc limit :limit", nativeQuery = true)
    List<LinkEntity> findStaleLinks(@Param("limit") long limit);

}
