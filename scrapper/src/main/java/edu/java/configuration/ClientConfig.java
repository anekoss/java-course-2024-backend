package edu.java.configuration;

import edu.java.client.GitHubClient;
import edu.java.client.StackOverflowClient;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

@Configuration
public class ClientConfig {

    @Bean
    public GitHubClient gitHubClient(@Value("${app.client.github.base-url}") String url) {
        return new GitHubClient(url);
    }

    @Bean
    public StackOverflowClient stackOverflowClient(@Value("${app.client.stackOverflow.base-url}") String url) {
        return new StackOverflowClient(url);
    }
}
