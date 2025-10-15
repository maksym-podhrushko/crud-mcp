package com.example.mcp_server.tools.dto;

/**
 * A data transfer object (DTO) representing the arguments required to update an
 * existing wiki entry.
 * <p>
 * Encapsulates the fields that can be updated for a wiki entry.
 * </p>
 *
 */
public record UpdateEntryArgs(Long id, String title, String content) {
}
