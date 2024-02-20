package edu.java.client;

import edu.java.configuration.ApplicationConfig;
import edu.java.configuration.ClientConfig;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

@EnableConfigurationProperties(ClientConfig.class)
public interface Client {
}
