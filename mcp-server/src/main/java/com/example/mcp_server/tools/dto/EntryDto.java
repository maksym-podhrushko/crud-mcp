package com.example.mcp_server.tools.dto;

/**
 * A data transfer object (DTO) representing a wiki entry.
 * <p>
 * Used for transferring wiki entry data between application layers.
 * </p>
 *
 */
public record EntryDto(Long id, String title, String content) {
}
