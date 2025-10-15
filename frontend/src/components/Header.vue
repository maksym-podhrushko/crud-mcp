<template>
  <header class="header">
    <div class="logo">
      <h1>Wiki Crud</h1>
    </div>
    <div class="nav-links">
      <router-link to="/">Home</router-link>
      <router-link to="/">Wiki</router-link>
      <!-- Add the chat toggle button -->
      <button class="chat-toggle-btn" @click="toggleChat" title="Chat with AI">
        <svg
          viewBox="0 0 24 24"
          width="24"
          height="24"
          stroke="currentColor"
          fill="none"
          stroke-width="2"
        >
          <path
            d="M21 15a2 2 0 0 1-2 2H7l-4 4V5a2 2 0 0 1 2-2h14a2 2 0 0 1 2 2z"
          ></path>
        </svg>
      </button>
    </div>
  </header>
</template>

<script>
export default {
  name: "AppHeader",
  methods: {
    toggleChat() {
      const emitter = this.$root.emitter;
      if (emitter) {
        emitter.emit("toggle-chat-sidebar");
      } else {
        console.error("Event emitter not found!");
      }
    },
  },
};
</script>

<style>
.header {
  display: flex;
  justify-content: space-between;
  align-items: center;
  padding: 1rem;
  background-color: #f8f9fa;
  border-bottom: 1px solid #e9ecef;
}

.logo {
  display: flex;
  align-items: center;
}

.logo img {
  height: 40px;
  margin-right: 10px;
}

.logo h1 {
  margin: 0;
  font-size: 1.5rem;
}

.nav-links {
  display: flex;
  align-items: center;
}

.nav-links a {
  margin-right: 15px;
  text-decoration: none;
  color: #495057;
}

.nav-links a:hover {
  color: #007bff;
}

.chat-toggle-btn {
  background: none;
  border: none;
  cursor: pointer;
  padding: 8px;
  color: #495057;
  border-radius: 50%;
  display: flex;
  align-items: center;
  justify-content: center;
  transition: background-color 0.3s;
}

.chat-toggle-btn:hover {
  background-color: rgba(0, 0, 0, 0.05);
}
</style>
