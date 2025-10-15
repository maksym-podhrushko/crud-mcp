package com.example.mcp_client;

import org.springframework.ai.chat.client.ChatClient;
import org.springframework.ai.chat.client.advisor.MessageChatMemoryAdvisor;
import org.springframework.ai.chat.memory.MessageWindowChatMemory;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * REST controller for handling chat-related endpoints in the MCP Client
 * application.
 * <p>
 * Provides endpoints for sending chat requests and receiving responses.
 * </p>
 *
 */

@RestController
@RequestMapping("/api/chat")
@CrossOrigin(origins = "*")
public class ChatController {

    private final ChatClient chatClient;
    private final MessageWindowChatMemory memory;

    public ChatController(ChatClient chatClient, MessageWindowChatMemory memory) {
        this.chatClient = chatClient;
        this.memory = memory;
    }

    public record ChatRequestWithMemory(String message) {
    }

    public record ChatResponseWithMemory(String reply) {
    }

    @PostMapping("/message")
    public ResponseEntity<ChatResponseWithMemory> sendMessage(@RequestBody ChatRequestWithMemory request) {

        // Attach per-session memory (remove if u want without memory)
        var advisor = MessageChatMemoryAdvisor
                .builder(memory)
                .build();

        String reply = chatClient.prompt()
                .advisors(spec -> spec.advisors(advisor))
                .user(request.message())
                .call()
                .content();

        return ResponseEntity.ok(new ChatResponseWithMemory(reply));
    }

}
