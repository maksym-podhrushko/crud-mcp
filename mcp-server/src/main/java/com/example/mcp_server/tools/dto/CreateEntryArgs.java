package com.example.mcp_server.tools.dto;

/**
 * A data transfer object (DTO) representing the arguments required to create a
 * new wiki entry.
 * <p>
 * Encapsulates the title and content of the entry to be created.
 * </p>
 */
public record CreateEntryArgs(String title, String content) {
}
