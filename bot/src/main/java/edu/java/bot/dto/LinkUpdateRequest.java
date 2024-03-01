package edu.java.bot.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.Min;
import jakarta.validation.constraints.NotEmpty;
import jakarta.validation.constraints.NotNull;
import org.apache.logging.log4j.core.config.plugins.validation.constraints.NotBlank;
import org.hibernate.validator.constraints.URL;


public record LinkUpdateRequest(@Min(0) @JsonProperty("id") Long id,
                                @NotBlank @URL @JsonProperty("url") String uri,
                                @JsonProperty("description") String description,
                                @NotNull @NotEmpty @JsonProperty("tgChatIds") Long[] tgChatIds) {

}
