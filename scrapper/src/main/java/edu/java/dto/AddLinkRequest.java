package edu.java.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;

public record AddLinkRequest(@NotBlank @URL @JsonProperty("link") String link) {
}
