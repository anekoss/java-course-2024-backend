package edu.java.client.dto;

import com.fasterxml.jackson.annotation.JsonProperty;
import jakarta.validation.constraints.NotBlank;
import java.util.List;

public record GitHubBranchResponse(List<Branch> branches) {

    public record Branch(@JsonProperty("name") @NotBlank String name) {

    }

}
