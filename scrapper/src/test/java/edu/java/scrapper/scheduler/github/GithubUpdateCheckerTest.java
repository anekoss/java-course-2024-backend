package edu.java.scrapper.scheduler.github;

import edu.java.domain.LinkEntity;
import edu.java.domain.LinkType;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.github.GithubResponseHandler;
import edu.java.scheduler.github.GithubUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.Mockito.when;

public class GithubUpdateCheckerTest {
    private final GithubResponseHandler responseHandler = Mockito.mock(GithubResponseHandler.class);
    private final GithubUpdateChecker updateChecker = new GithubUpdateChecker(responseHandler);
    private final LinkEntity link =
        new LinkEntity().setId(1L).setUri(URI.create("https://github.com/anekoss/tinkoff-project"))
                        .setLinkType(LinkType.GITHUB)
                        .setUpdatedAt(OffsetDateTime.now()).setCheckedAt(OffsetDateTime.now());
    private final String owner = "anekoss";
    private final String repos = "tinkoff-project";

    @Test
    void testCheck_shouldReturnUpdateIfUpdate() {
        when(responseHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.UPDATE
        ));
        assert updateChecker.check(link).type() == UpdateType.UPDATE;
    }

    @Test
    void testCheck_shouldReturnNoUpdateIfNoUpdate() {
        when(responseHandler.handle(owner, repos, link)).thenReturn(new LinkUpdate(
            link,
            UpdateType.NO_UPDATE
        ));
        assert updateChecker.check(link).type() == UpdateType.NO_UPDATE;
    }

    @Test
    void testCheck_shouldReturnNoUpdateIfInvalidUri() {
        link.setUri(URI.create("https://github.com/anekoss"));
        assert updateChecker.check(link).type() == UpdateType.NO_UPDATE;
    }
}
