package com.example.mcp_server.tools;

import org.springframework.ai.tool.annotation.Tool;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Service;
import org.springframework.web.reactive.function.client.WebClient;
import org.springframework.web.reactive.function.client.WebClientResponseException;

import com.example.mcp_server.tools.dto.CreateEntryArgs;
import com.example.mcp_server.tools.dto.EntryDto;
import com.example.mcp_server.tools.dto.UpdateEntryArgs;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class providing helper methods for wiki-related operations.
 * <p>
 * Used by services and controllers to perform common wiki tasks.
 * </p>
 *
 */

@Service
public class WikiTools {
    private static final Logger logger = LoggerFactory.getLogger(WikiTools.class);
    private final WebClient wiki;

    public WikiTools(@Qualifier("wikiWebClient") WebClient wikiClient) {
        this.wiki = wikiClient;
    }

    @Tool(description = "Create a Wiki entry; returns the created entry")
    public EntryDto createEntry(CreateEntryArgs args) {
        return wiki
                .post()
                .uri("api/wiki")
                .bodyValue(args)
                .retrieve()
                .bodyToMono(EntryDto.class)
                .block();
    }

    @Tool(description = "Get a wiki entry by ID; returns null if not found")
    public EntryDto getEntry(Long id) {
        try {
            return wiki
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/api/wiki/{id}").build(id))
                    .retrieve().bodyToMono(EntryDto.class).block();
        } catch (Exception e) {
            return null;
        }
    }

    @Tool(description = "List all wiki entries")
    public EntryDto[] listEntries() {
        return wiki
                .get()
                .uri("/api/wiki")
                .retrieve().bodyToMono(EntryDto[].class).block();
    }

    @Tool(description = "Fetch a wiki page by title from Wikipedia!; returns null if not found")
    public EntryDto fetchWikiPage(String title) {
        try {
            return wiki
                    .get()
                    .uri(uriBuilder -> uriBuilder.path("/api/wiki/fetch").queryParam("title", title).build())
                    .retrieve()
                    .bodyToMono(EntryDto.class)
                    .onErrorResume(WebClientResponseException.class, e -> {
                        logger.error("Error fetching wiki page: {} - {}", e.getStatusCode(), e.getMessage());
                        return reactor.core.publisher.Mono.empty();
                    })
                    .block();
        } catch (Exception e) {
            logger.error("Exception while fetching wiki page", e);
            return null;
        }
    }

    @Tool(description = "Update a wiki entry by ID; returns the updated entry")
    public EntryDto updateEntry(Long id, String title, String content) {
        EntryDto existingEntry = getEntry(id);
        if (existingEntry == null) {
            return null;
        }

        String newTitle = (title != null) ? title : existingEntry.title();
        String newContent = (content != null) ? content : existingEntry.content();

        return wiki
                .put().uri("api/wiki/{id}", id)
                .bodyValue(new UpdateEntryArgs(id, newTitle, newContent))
                .retrieve().bodyToMono(EntryDto.class)
                .block();
    }

    @Tool(description = "Delete a wiki entry by ID")
    public void deleteEntry(Long id) {
        wiki
                .delete()
                .uri("/api/wiki/{id}", id)
                .retrieve().bodyToMono(Void.class).block();
    }

}
