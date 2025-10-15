package com.example.mcp_server.tools.dto;

public class ToolDescription {
    private String name;
    private String description;

    public ToolDescription(String name, String description) {
        this.name = name;
        this.description = description;
    }

    public String getName() {
        return name;
    }

    public String getDescription() {
        return description;
    }
}
