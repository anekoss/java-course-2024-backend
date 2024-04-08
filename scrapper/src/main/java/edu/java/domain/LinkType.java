package edu.java.domain;

import lombok.Getter;

@Getter
public enum LinkType {
    STACKOVERFLOW("stackoverflow.com"), GITHUB("github.com");

    private final String host;

    LinkType(String host) {
        this.host = host;
    }

}
