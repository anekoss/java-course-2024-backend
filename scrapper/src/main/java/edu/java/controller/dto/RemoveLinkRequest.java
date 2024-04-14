package edu.java.controller.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.experimental.Accessors;

@Accessors(chain = true)
public record RemoveLinkRequest(@NotBlank String link) {
}
