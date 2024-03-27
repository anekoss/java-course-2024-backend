package edu.java.repository;

import java.util.Optional;

public interface StackOverflowLinkRepository {

    Optional<Long> findStackOverflowAnswerCountByLinkId(Long linkId);

    int add(Long linkId, Long count);

    int update(Long linkId, Long count);
}
