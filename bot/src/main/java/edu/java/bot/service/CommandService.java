package edu.java.bot.service;

import edu.java.bot.client.LinksClient;
import edu.java.bot.client.TgChatClient;
import edu.java.bot.client.dto.AddLinkRequest;
import edu.java.bot.client.dto.LinkResponse;
import edu.java.bot.client.dto.ListLinksResponse;
import edu.java.bot.client.dto.RemoveLinkRequest;
import edu.java.bot.client.exception.BadResponseException;
import edu.java.bot.commands.CommandExecutionStatus;
import edu.java.bot.service.validator.Validator;
import java.net.URI;
import java.util.Arrays;
import java.util.Set;
import java.util.stream.Collectors;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_ALREADY_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_INVALID;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_LINK_NOT_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.FAIL_USER_ALREADY_REGISTER;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_LINK_UN_TRACK;
import static edu.java.bot.commands.CommandExecutionStatus.SUCCESS_REGISTER;

@Service
@RequiredArgsConstructor
public class CommandService {
    private final TgChatClient tgChatClient;
    private final LinksClient linksClient;
    private final Validator urlValidator;

    public CommandExecutionStatus start(Long id) {
        try {
            tgChatClient.registerChat(id);
            return SUCCESS_REGISTER;
        } catch (BadResponseException e) {
            return FAIL_USER_ALREADY_REGISTER;
        }
    }

    public CommandExecutionStatus track(Long id, String url) {
        if (urlValidator.isValid(url).isEmpty()) {
            return FAIL_LINK_INVALID;
        }
        try {
            linksClient.addLink(id, new AddLinkRequest(url));
            return SUCCESS_LINK_TRACK;
        } catch (BadResponseException e) {
            return FAIL_LINK_ALREADY_TRACK;
        }
    }

    public CommandExecutionStatus unTrack(Long id, String url) {
        if (urlValidator.isValid(url).isEmpty()) {
            return FAIL_LINK_INVALID;
        }
        try {
            linksClient.deleteLink(id, new RemoveLinkRequest(url));
            return SUCCESS_LINK_UN_TRACK;
        } catch (BadResponseException e) {
            return FAIL_LINK_NOT_TRACK;
        }
    }

    public Set<URI> list(Long id) {
        try {
            ListLinksResponse listLinksResponse = linksClient.getLinks(id);
            return Arrays.stream(listLinksResponse.linkResponses())
                         .map(LinkResponse::uri)
                         .collect(Collectors.toSet());
        } catch (BadResponseException e) {
            return Set.of();
        }
    }

    public boolean stop(Long id) throws BadResponseException {
        try {
            tgChatClient.deleteChat(id);
            return true;
        } catch (BadResponseException e) {
            return false;
        }
    }

}
