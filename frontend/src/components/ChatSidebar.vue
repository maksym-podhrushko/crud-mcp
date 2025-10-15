<template>
  <div class="chat-sidebar" :class="{ 'chat-sidebar-open': isOpen }">
    <div class="chat-sidebar-header">
      <h2>AI Agent</h2>
      <button @click="toggleSidebar" class="close-button">
        <svg
          width="24"
          height="24"
          viewBox="0 0 24 24"
          fill="none"
          stroke="currentColor"
          stroke-width="2"
        >
          <line x1="18" y1="6" x2="6" y2="18"></line>
          <line x1="6" y1="6" x2="18" y2="18"></line>
        </svg>
      </button>
    </div>

    <div class="chat-container" ref="chatContainer">
      <div
        v-for="(msg, idx) in messages"
        :key="idx"
        :class="[
          'message-item',
          msg.role == 'user' ? 'user-message' : 'bot-message',
        ]"
      >
        <div class="message-bubble">
          <div v-html="formatMessage(msg.text)" class="message-content"></div>
        </div>
      </div>
    </div>

    <div class="input-container">
      <div class="input-wrapper">
        <div class="mb-3 position-relative">
          <textarea
            v-model="userMessage"
            class="form-control"
            placeholder="Type your message..."
            rows="2"
            @keyup.enter="sendMessage"
          ></textarea>

          <button
            @click="startNewChat"
            class="new-chat-button"
            title="Start New Chat"
          >
            <svg
              viewBox="0 0 24 24"
              width="16"
              height="16"
              stroke="currentColor"
              fill="none"
              stroke-width="2"
            >
              <path
                d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
              ></path>
              <line x1="12" y1="8" x2="12" y2="16"></line>
              <line x1="8" y1="12" x2="16" y2="12"></line>
            </svg>
          </button>

          <button
            @click="sendMessage"
            :disabled="loading || !userMessage"
            class="send-button"
          >
            <svg
              xmlns="http://www.w3.org/2000/svg"
              width="16"
              height="16"
              fill="currentColor"
              viewBox="0 0 16 16"
            >
              <path
                d="M15.964.686a.5.5 0 0 0-.65-.65L.767 5.855H.766l-.452.18a.5.5 0 0 0-.082.887l.41.26.001.002 4.995 3.178 3.178 4.995.002.002.26.41a.5.5 0 0 0 .886-.083l6-15Zm-1.833 1.89L6.637 10.07l-.215-.338a.5.5 0 0 0-.154-.154l-.338-.215 7.494-7.494 1.178-.471-.47 1.178Z"
              />
            </svg>
          </button>
        </div>
      </div>
      <div v-if="error" class="text-danger mb-2">{{ error }}</div>
    </div>
  </div>
</template>

<script>
import { sendChat } from "../services/ChatService";

export default {
  name: "ChatSidebar",
  props: {
    isOpen: {
      type: Boolean,
      default: false,
    },
  },
  data() {
    return {
      userMessage: "",
      messages: [],
      loading: false,
      error: null,
      chatId: "default-chat", // Default chat ID
    };
  },
  methods: {
    toggleSidebar() {
      this.$emit("toggle-sidebar");
    },
    async sendMessage() {
      if (!this.userMessage.trim()) return;
      const message = this.userMessage;
      this.messages.push({ role: "user", text: message });
      this.userMessage = "";
      this.loading = true;
      this.error = null;

      this.saveMessagesToLocalStorage();
      // Scroll to bottom after adding user message
      this.scrollToBottom();

      try {
        const res = await sendChat(message);
        this.messages.push({
          role: "bot",
          text: res.reply || JSON.stringify(res),
        });

        // Scroll to bottom again after response is received
        this.scrollToBottom();
        this.saveMessagesToLocalStorage();
      } catch (error) {
        this.error = "Failed to send message";
      } finally {
        this.loading = false;
      }
    },
    formatMessage(text) {
      if (!text) return "";
      // Handle <think> tags - make them collapsible
      let formattedText = text.replace(
        /<think>([\s\S]*?)<\/think>/g,
        '<div class="think-block collapsed">' +
          '<div class="think-header" onclick="this.parentNode.classList.toggle(\'collapsed\')">' +
          '<strong>Thinking:</strong> <span class="toggle-icon">▼</span>' +
          "</div>" +
          '<div class="think-content">$1</div>' +
          "</div>"
      );

      // Handle code blocks with syntax highlighting
      formattedText = formattedText.replace(
        /```(\w*)([\s\S]*?)```/g,
        (match, language, code) => {
          return `<pre class="code-block ${language}"><code>${this.escapeHtml(
            code.trim()
          )}</code></pre>`;
        }
      );

      // Handle inline code
      formattedText = formattedText.replace(/`([^`]+)`/g, "<code>$1</code>");

      // Convert URLs to links
      formattedText = formattedText.replace(
        /(https?:\/\/[^\s]+)/g,
        '<a href="$1" target="_blank" rel="noopener">$1</a>'
      );

      // Convert line breaks to <br> tags
      formattedText = formattedText.replace(/\n/g, "<br>");

      return formattedText;
    },
    escapeHtml(text) {
      return text
        .replace(/&/g, "&amp;")
        .replace(/</g, "&lt;")
        .replace(/>/g, "&gt;")
        .replace(/"/g, "&quot;")
        .replace(/'/g, "&#039;");
    },
    scrollToBottom() {
      if (this.$refs.chatContainer) {
        this.$nextTick(() => {
          this.$refs.chatContainer.scrollTop =
            this.$refs.chatContainer.scrollHeight;
        });
      }
    },
    saveMessagesToLocalStorage() {
      if (!this.chatId) {
        this.chatId = "chat-" + Date.now();
      }
      localStorage.setItem(this.chatId, JSON.stringify(this.messages));

      let chatIds = JSON.parse(localStorage.getItem("chatIds") || "[]");
      if (!chatIds.includes(this.chatId)) {
        chatIds.push(this.chatId);
        localStorage.setItem("chatIds", JSON.stringify(chatIds));
      }
    },
    loadMessagesFromLocalStorage() {
      // Try to get the most recent chat
      let chatIds = JSON.parse(localStorage.getItem("chatIds") || "[]");

      if (chatIds.length > 0) {
        // Get the last chat ID
        this.chatId = chatIds[chatIds.length - 1];
        const savedMessages = localStorage.getItem(this.chatId);

        if (savedMessages) {
          try {
            this.messages = JSON.parse(savedMessages);
          } catch (e) {
            console.error("Failed to parse saved messages:", e);
            this.messages = [];
          }
        }
      }
    },
    startNewChat() {
      if (this.messages.length > 0) {
        if (!confirm("Start a new chat?")) {
          return;
        }
      }

      this.chatId = "chat-" + Date.now();
      this.messages = [];
      this.userMessage = "";
      this.error = null;

      let chatIds = JSON.parse(localStorage.getItem("chatIds") || "[]");
      chatIds.push(this.chatId);
      localStorage.setItem("chatIds", JSON.stringify(chatIds));
    },
    checkLocalStorageAvailability() {
      const test = "test";
      try {
        localStorage.setItem(test, test);
        localStorage.removeItem(test);
        return true;
      } catch (e) {
        return false;
      }
    },
  },
  watch: {
    messages: {
      handler() {
        this.scrollToBottom();
      },
      deep: true,
    },
    isOpen(newVal) {
      if (newVal) {
        this.scrollToBottom();
      }
    },
  },
  mounted() {
    // Check if localStorage is available
    const hasLocalStorage = this.checkLocalStorageAvailability();

    if (hasLocalStorage) {
      // Load saved messages when component mounts
      this.loadMessagesFromLocalStorage();
    } else {
      console.warn(
        "LocalStorage is not available. Chat persistence will not work."
      );
    }

    this.scrollToBottom();
  },
};
</script>

<style scoped>
.chat-sidebar {
  position: fixed;
  top: 0;
  right: -25%; /* Start offscreen - same as width */
  width: 25%;
  height: 100vh;
  background-color: white;
  box-shadow: -2px 0 5px rgba(0, 0, 0, 0.1);
  display: flex;
  flex-direction: column;
  z-index: 1000;
  transition: right 0.3s ease;
}

.chat-sidebar-open {
  right: 0;
}

.chat-sidebar-header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 15px;
  border-bottom: 1px solid #e0e0e0;
}

.close-button {
  background: none;
  border: none;
  cursor: pointer;
  color: #666;
}

.close-button:hover {
  color: #333;
}

.chat-container {
  display: flex;
  flex-direction: column;
  flex: 1;
  overflow-y: auto;
  padding: 15px;
  background-color: #f8f9fa;
}

.message-item {
  display: flex;
  margin-bottom: 15px;
  width: 100%;
}

.bot-message {
  justify-content: flex-start;
}

.user-message {
  justify-content: flex-end;
}

.message-bubble {
  max-width: 70%;
  padding: 10px 15px;
  border-radius: 18px;
  word-break: break-word;
  text-align: left;
}

.user-message .message-bubble {
  background-color: #e9ecef;
  border-bottom-right-radius: 5px;
}

.bot-message .message-bubble {
  background-color: #f1f3f5;
  border-bottom-left-radius: 5px;
}

.input-container {
  padding: 15px;
  border-top: 1px solid #e0e0e0;
}

.input-wrapper {
  position: relative;
  width: 100%;
}

.form-control {
  width: 100%;
  padding: 12px;
  padding-right: 90px;
  border: 1px solid #ccc;
  border-radius: 24px;
  resize: none;
  font-family: inherit;
  font-size: 1rem;
  line-height: 1.5;
}

.send-button {
  position: absolute;
  right: 10px;
  bottom: 50%;
  transform: translateY(50%);
  width: 36px;
  height: 36px;
  border-radius: 25%;
  background-color: #007bff;
  color: white;
  border: none;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: background-color 0.3s;
}

.send-button:hover {
  background-color: #0069d9;
}

.send-button:disabled {
  background-color: #cccccc;
  cursor: not-allowed;
}

.new-chat-button {
  position: absolute;
  right: 56px;
  bottom: 50%;
  transform: translateY(50%);
  width: 36px;
  height: 36px;
  border-radius: 25%;
  background-color: #f1f3f5;
  color: #495057;
  border: 1px solid #ced4da;
  display: flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  transition: all 0.2s;
  z-index: 10;
}

.new-chat-button:hover {
  background-color: #e9ecef;
  color: #212529;
}

.text-danger {
  color: #dc3545;
}

/* Responsive design */
@media (max-width: 1200px) {
  .chat-sidebar {
    width: 30%;
  }
}

@media (max-width: 768px) {
  .chat-sidebar {
    width: 100%;
    right: -100%;
  }

  .message-bubble {
    max-width: 85%;
  }
}
</style>
