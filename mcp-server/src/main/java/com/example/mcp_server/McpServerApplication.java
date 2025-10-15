package com.example.mcp_server;

import com.example.mcp_server.tools.WikiTools;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.ai.tool.ToolCallbackProvider;
import org.springframework.ai.tool.method.MethodToolCallbackProvider;

/**
 * The entry point for the MCP Server Spring Boot application.
 * <p>
 * This class bootstraps the application and configures beans required for tool
 * callback providers.
 * </p>
 */

@SpringBootApplication
public class McpServerApplication {
	public static void main(String[] args) {
		SpringApplication.run(McpServerApplication.class, args);
	}

	/**
	 * Provides a {@link ToolCallbackProvider} bean for WikiTools.
	 *
	 * @param tools the WikiTools instance
	 * @return a configured ToolCallbackProvider
	 */

	@Bean
	public ToolCallbackProvider todoMcpTools(WikiTools tools) {
		return MethodToolCallbackProvider.builder()
				.toolObjects(tools)
				.build();
	}

}
