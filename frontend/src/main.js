import { createApp } from "vue";
import App from "./App.vue";
import "bootstrap/dist/css/bootstrap.min.css";
import router from "./router";

const app = createApp(App);

// Create a simple event bus for Vue 3
app.config.globalProperties.emitter = {
  events: {},
  emit(event, ...args) {
    if (this.events[event]) {
      this.events[event].forEach((callback) => callback(...args));
    }
  },
  on(event, callback) {
    if (!this.events[event]) {
      this.events[event] = [];
    }
    this.events[event].push(callback);
  },
  off(event, callback) {
    if (this.events[event]) {
      this.events[event] = this.events[event].filter((cb) => cb !== callback);
    }
  },
};

// Set up SSE connection

// Use relative path for Docker (Nginx proxy), absolute for local dev
const BASE_URL = process.env.VUE_APP_API_URL || "/api";
const eventSource = new EventSource(`${BASE_URL}/events`);

eventSource.addEventListener("notify-wiki", (event) => {
  const pageId = JSON.parse(event.data);
  app.config.globalProperties.emitter.emit("notify-wiki", pageId);
});

eventSource.onerror = (err) => {
  console.error("EventSource failed:", err);
  setTimeout(() => {
    // Try to reconnect after 5 seconds
    new EventSource(`${BASE_URL}/api/events`);
  }, 5000);
};

app.use(router);
app.mount("#app");
