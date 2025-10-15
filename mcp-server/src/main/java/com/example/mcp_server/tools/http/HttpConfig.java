package com.example.mcp_server.tools.http;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.reactive.function.client.WebClient;

/**
 * Configuration class for HTTP clients.
 * <p>
 * Defines beans for making HTTP requests to external services, such as the wiki
 * backend.
 * </p>
 *
 */

@Configuration
public class HttpConfig {
    @Bean(name = "wikiWebClient")
    WebClient wikiClient(
            @Value("${WIKI_BACKEND_BASE_URL:http://backend:8080}") String baseUrl) {
        return WebClient.builder().baseUrl(baseUrl).build();
    }
}
