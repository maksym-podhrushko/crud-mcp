<template>
  <div id="app" :class="{ 'with-sidebar': isChatOpen }">
    <AppHeader />
    <div class="content">
      <router-view></router-view>
    </div>
    <ChatSidebar :isOpen="isChatOpen" @toggle-sidebar="toggleChatSidebar" />
  </div>
</template>

<script>
import AppHeader from "./components/Header.vue";
import ChatSidebar from "./components/ChatSidebar.vue";

export default {
  name: "App",
  components: {
    AppHeader,
    ChatSidebar,
  },
  data() {
    return {
      isChatOpen: false,
    };
  },
  methods: {
    toggleChatSidebar() {
      this.isChatOpen = !this.isChatOpen;
    },
  },
  mounted() {
    this.emitter = this.$root.emitter || this.emitter;
    this.emitter.on("toggle-chat-sidebar", this.toggleChatSidebar);
  },
  beforeUnmount() {
    this.emitter.off("toggle-chat-sidebar", this.toggleChatSidebar);
  },
};
</script>

<style>
#app {
  font-family: Avenir, Helvetica, Arial, sans-serif;
  -webkit-font-smoothing: antialiased;
  -moz-osx-font-smoothing: grayscale;
  color: #2c3e50;
  transition: margin-right 0.3s ease;
}

#app.with-sidebar {
  margin-right: 0%; /* Same as sidebar width */
}

.content {
  padding: 20px;
  max-width: 1200px;
  margin: 0 auto;
  transition: margin-right 0.3s ease;
}

body {
  margin: 0;
  padding: 0;
  overflow-x: hidden; /* Prevent horizontal scroll when sidebar opens */
}

/* Think block styling */
.think-block {
  background-color: #f0f8ff;
  border-left: 3px solid #1e90ff;
  margin: 10px 0;
  padding: 10px;
  border-radius: 4px;
  transition: all 0.3s ease;
}

.think-header {
  cursor: pointer;
  user-select: none;
  display: flex;
  justify-content: space-between;
  align-items: center;
}

.toggle-icon {
  transition: transform 0.3s ease;
  font-size: 0.8em;
}

.think-content {
  margin-top: 8px;
  color: #555;
  max-height: 1000px;
  overflow: hidden;
  transition: max-height 0.5s ease, opacity 0.3s ease, margin 0.3s ease;
  opacity: 1;
}

.think-block.collapsed .think-content {
  max-height: 0;
  margin-top: 0;
  opacity: 0;
}

.think-block.collapsed .toggle-icon {
  transform: rotate(-90deg);
}

header.header {
  position: sticky;
  top: 0;
  z-index: 100;
  background-color: white;
  transition: margin-right 0.3s ease;
}

#app.with-sidebar header.header {
  margin-right: 25%;
}

/* Responsive design */
@media (max-width: 1200px) {
  #app.with-sidebar {
    margin-right: 30%;
  }

  #app.with-sidebar header.header {
    margin-right: 25%;
  }
}

@media (max-width: 768px) {
  #app.with-sidebar {
    margin-right: 0;
  }

  #app.with-sidebar header.header {
    margin-right: 0;
  }
}
</style>
