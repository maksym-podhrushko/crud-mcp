package com.example.mcp_client;

import java.util.List;
import io.modelcontextprotocol.client.McpSyncClient;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.ai.mcp.SyncMcpToolCallbackProvider;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;

/**
 * The entry point for the MCP Client Spring Boot application.
 * <p>
 * Configures beans for chat client and chat memory, enabling interaction with
 * MCP tools.
 * </p>
 */
@SpringBootApplication
public class Application {

	/**
	 * Starts the MCP Client application.
	 *
	 * @param args command-line arguments
	 */
	public static void main(String[] args) {
		SpringApplication.run(Application.class, args);
	}

	/**
	 * Provides a {@link ChatClient} bean configured with MCP tool callbacks and
	 * chat memory advisors.
	 *
	 * @param builder        the ChatClient builder
	 * @param mcpSyncClients the list of MCP sync clients
	 * @return a configured ChatClient instance
	 */

	@Bean
	ChatClient chatClient(ChatClient.Builder builder, List<McpSyncClient> mcpSyncClients) {

		return builder
				.defaultSystem("You are very helpful AI assistant.")
				.defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
				.defaultAdvisors(
						MessageChatMemoryAdvisor.builder(
								MessageWindowChatMemory.builder().maxMessages(30).build()).build())
				.build();
	}

	/**
	 * Provides a {@link MessageWindowChatMemory} bean for chat memory management.
	 *
	 * @return a configured MessageWindowChatMemory instance
	 */
	@Bean
	MessageWindowChatMemory chatMemory() {
		return MessageWindowChatMemory.builder().maxMessages(30).build();
	}

	// @Bean
	// public CommandLineRunner chatbot(ChatClient.Builder builder,
	// List<McpSyncClient> mcpSyncClients) {
	// return args -> {
	// ChatClient chat = builder
	// .defaultSystem("You are a helpful assistant that can operate a simple wiki
	// CRUD via MCP tools.")
	// .defaultToolCallbacks(new SyncMcpToolCallbackProvider(mcpSyncClients))
	// .defaultAdvisors(
	// MessageChatMemoryAdvisor.builder(
	// MessageWindowChatMemory.builder().maxMessages(30).build()).build())
	// .build();
	// System.out.println("Assistant ready. Type 'exit' to quit.");
	// try (Scanner scanner = new Scanner(System.in)) {
	// while (true) {
	// System.out.print("> ");
	// String line = scanner.nextLine();
	// if (line == null)
	// break;
	// line = line.trim();
	// if (line.equalsIgnoreCase("exit"))
	// break;
	// try {
	// String reply = chat.prompt().user(line).call().content();
	// System.out.println(reply);
	// } catch (Exception e) {
	// System.out.println("Error: " + e.getMessage());
	// }
	// }
	// }
	// };
	// }

}
