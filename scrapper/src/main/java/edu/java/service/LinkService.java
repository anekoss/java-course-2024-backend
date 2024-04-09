package edu.java.service;

import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.exception.ChatNotFoundException;
import edu.java.controller.exception.LinkAlreadyExistException;
import edu.java.controller.exception.LinkNotFoundException;
import edu.java.domain.GithubLink;
import edu.java.domain.Link;
import edu.java.domain.StackOverflowLink;
import edu.java.scheduler.dto.UpdateType;
import java.net.URI;
import java.time.OffsetDateTime;
import java.util.List;

public interface LinkService {
    LinkResponse add(long tgChatId, URI url) throws ChatNotFoundException, LinkAlreadyExistException;

    LinkResponse remove(long tgChatId, URI url) throws ChatNotFoundException, LinkNotFoundException;

    ListLinksResponse listAll(long tgChatId) throws ChatNotFoundException;

    long[] getChatIdsByLinkId(long linkId);

    long update(long id, OffsetDateTime updatedAt, OffsetDateTime checkedAt);

    List<Link> getStaleLinks(long limit);

    UpdateType updateStackOverflowAnswerCount(StackOverflowLink stackOverflowLink);

    UpdateType updateGithubBranchCount(GithubLink githubLink);

}
