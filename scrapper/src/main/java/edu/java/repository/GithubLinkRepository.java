package edu.java.repository;

import java.util.Optional;

public interface GithubLinkRepository {

     Optional<Long> findGithubBranchCountByLinkId(Long linkId);

    int add(Long linkId, Long count);

    int update(Long linkId, Long count);
}
