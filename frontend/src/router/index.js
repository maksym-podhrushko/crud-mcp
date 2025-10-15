import { createRouter, createWebHistory } from "vue-router";
import WikiPage from "../components/WikiPage.vue";

// Define routes
const routes = [
  {
    path: "/",
    name: "Home",
    component: WikiPage,
  },
  {
    path: "/wiki",
    name: "Wiki",
    component: WikiPage,
  },
];

// Create router instance
const router = createRouter({
  history: createWebHistory(),
  routes,
});

export default router;
