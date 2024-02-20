package edu.java.client;

import org.springframework.beans.factory.annotation.Value;

public class GitHubClient {
    @Value(value = "${github.baseUrl}")
    private String defaultUrl;



}
