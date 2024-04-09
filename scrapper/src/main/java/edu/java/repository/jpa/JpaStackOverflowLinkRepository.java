package edu.java.repository.jpa;

import edu.java.domain.StackOverflowLinkEntity;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;

public interface JpaStackOverflowLinkRepository extends JpaRepository<StackOverflowLinkEntity, Long> {

    Optional<StackOverflowLinkEntity> findByLink_Id(Long linkId);

    boolean existsByLink_Id(Long linkId);
}
