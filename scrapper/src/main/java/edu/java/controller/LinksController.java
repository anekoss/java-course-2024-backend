package edu.java.controller;

import edu.java.controller.dto.AddLinkRequest;
import edu.java.controller.dto.LinkResponse;
import edu.java.controller.dto.ListLinksResponse;
import edu.java.controller.dto.RemoveLinkRequest;
import edu.java.domain.Link;
import edu.java.exception.AlreadyExistException;
import edu.java.exception.ChatNotFoundException;
import edu.java.service.LinkService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;
import lombok.AllArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;
import static org.springframework.http.MediaType.APPLICATION_JSON_VALUE;

@Slf4j
@RestController
@AllArgsConstructor
public class LinksController {
    private final LinkService linkService;

    @GetMapping(path = "/links", produces = APPLICATION_JSON_VALUE)
    public ResponseEntity<ListLinksResponse> getLinks(
        @RequestHeader(value = "Tg-Chat-Id") Long tgChatId
    ) {
        List<Link> links = linkService.listAll(tgChatId);
        LinkResponse[] linkResponses = new LinkResponse[links.size()];
        for (int i = 0; i < links.size(); i++) {
            linkResponses[i] = new LinkResponse((long) i, links.get(i).getUri());
        }
        log.info("Ссылки успешно получены {}", tgChatId);
        return ResponseEntity.ok(new ListLinksResponse(linkResponses, (long) links.size()));
    }

    @DeleteMapping(path = "/links", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkResponse> deleteLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull Long tgChatId, @RequestBody @Valid RemoveLinkRequest request
    ) throws URISyntaxException, ChatNotFoundException, AlreadyExistException {
        URI uri = new URI(request.link());
        linkService.remove(tgChatId, uri);
        log.info("Ссылка успешно убрана {}", request.link());
        return ResponseEntity.ok(new LinkResponse(tgChatId, uri));
    }

    @PostMapping(path = "/links", produces = APPLICATION_JSON_VALUE, consumes = APPLICATION_JSON_VALUE)
    public ResponseEntity<LinkResponse> addLink(
        @RequestHeader(value = "Tg-Chat-Id") @NotNull Long tgChatId,
        @RequestBody @Valid AddLinkRequest request
    ) throws URISyntaxException, ChatNotFoundException, AlreadyExistException {
        log.info("Ссылка успешно добавлена {}", request.link());
        URI uri = new URI(request.link());
        linkService.add(tgChatId, uri);
        return ResponseEntity.ok(new LinkResponse(tgChatId, uri));

    }

}
