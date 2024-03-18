package edu.java.service;

import edu.java.client.StackOverflowClient;
import edu.java.client.dto.StackOverflowResponse;
import edu.java.domain.Link;
import java.time.OffsetDateTime;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class StackOverflowUpdater {
    private final StackOverflowClient stackOverflowClient;

    public Link update(Link link) {
        int question = getQuestion(link.getUri().toString());
        if (question != -1) {
            StackOverflowResponse response = stackOverflowClient.fetchQuestion(question);
            if (response != null) {
                OffsetDateTime updatedAt = link.getUpdatedAt();
                for (StackOverflowResponse.StackOverflowItem item : response.items()) {
                    if (item.updatedAt().isAfter(updatedAt)) {
                        updatedAt = item.updatedAt();
                    }
                }
                link.setUpdatedAt(updatedAt);
                link.setUpdatedAt(OffsetDateTime.now());
            }
        }
        return link;
    }

    private int getQuestion(String uri) {
        Pattern pattern = Pattern.compile("/(\\d+)/");
        Matcher matcher = pattern.matcher(uri);
        if (matcher.find()) {
            return Integer.parseInt(matcher.group());
        }
        return -1;
    }

}
