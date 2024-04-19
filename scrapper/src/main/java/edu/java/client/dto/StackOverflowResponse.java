package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import java.time.OffsetDateTime;
import java.util.List;
import org.hibernate.validator.constraints.URL;

public record StackOverflowResponse(List<StackOverflowItem> items) {
    public record StackOverflowItem(
        @JsonProperty("question_id") @NotNull Long id,
        @NotBlank String title,
        @NotBlank @URL String link,
        @JsonProperty("answer_count") @NotNull Long countAnswer,
        @JsonProperty("creation_date") @NotNull OffsetDateTime createdAt,
        @JsonProperty("last_activity_date") @NotNull OffsetDateTime updatedAt
    ) {
    }
}
