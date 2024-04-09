package edu.java.scrapper.scheduler.stackoverflow;

import edu.java.domain.Link;
import edu.java.domain.LinkType;
import edu.java.scheduler.UpdateChecker;
import edu.java.scheduler.dto.LinkUpdate;
import edu.java.scheduler.dto.UpdateType;
import edu.java.scheduler.stackoverflow.StackOverflowResponseHandler;
import edu.java.scheduler.stackoverflow.StackOverflowUpdateChecker;
import java.net.URI;
import java.time.OffsetDateTime;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;
import static org.mockito.Mockito.when;

public class StackOverflowUpdateCheckerTest {

    private final StackOverflowResponseHandler responseHandler =
        Mockito.mock(StackOverflowResponseHandler.class);

    private final UpdateChecker updateChecker = new StackOverflowUpdateChecker(responseHandler);

    private final Link link =
        new Link().setId(1L)
                  .setUri(URI.create("https://stackoverflow.com/questions/78056352/react-leaflet-map-not-re-rendering"))
                  .setLinkType(LinkType.STACKOVERFLOW).setCheckedAt(OffsetDateTime.now())
                  .setUpdatedAt(OffsetDateTime.now());
    private final long question = 78056352L;

    @Test
    void testCheck_shouldReturnNoUpdateIfNoUpdate() {
        when(responseHandler.handle(question, any())).thenReturn(new LinkUpdate(link, UpdateType.NO_UPDATE));
        assert updateChecker.check(link).type() == UpdateType.NO_UPDATE;
    }

    @Test
    void testCheck_shouldReturnUpdateIfHaveUpdate() {
        when(responseHandler.handle(question, any())).thenReturn(new LinkUpdate(link, UpdateType.UPDATE));
        assert updateChecker.check(link).type() == UpdateType.UPDATE;
    }

    @Test
    void testCheck_shouldReturnUpdateAnswerIfHaveNewAnswer() {
        when(responseHandler.handle(question, any())).thenReturn(new LinkUpdate(link, UpdateType.UPDATE_ANSWER));
        assert updateChecker.check(link).type() == UpdateType.UPDATE_ANSWER;
    }

    @Test
    void testCheck_shouldReturnNoUpdateIfInvalidUri() {
        link.setUri(URI.create("https://stackoverflow.com/questions"));
        assert updateChecker.check(link).type() == UpdateType.NO_UPDATE;
    }

    @Test
    void testCheck_shouldCorrectlyReturnLongQuestion() {
        when(responseHandler.handle(question, any())).thenReturn(new LinkUpdate(link, UpdateType.UPDATE_ANSWER));
        assert updateChecker.check(link).type() == UpdateType.UPDATE_ANSWER;
        verify(responseHandler, times(1)).handle(question, link);
    }
}
