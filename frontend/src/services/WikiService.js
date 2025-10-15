import axios from "axios";

// Use VUE_APP_ prefix for environment variables to be available in Vue.js
// Falls back to /api/wiki if not set (works with nginx proxy)
const WIKI_API_BASE_URL = process.env.VUE_APP_WIKI_API_BASE_URL || "/api/wiki";

class WikiService {
  getPages() {
    return axios.get(WIKI_API_BASE_URL);
  }

  getPageById(id) {
    return axios.get(`${WIKI_API_BASE_URL}/${id}`);
  }

  createPage(page) {
    return axios.post(WIKI_API_BASE_URL, page);
  }

  updatePage(id, page) {
    return axios.put(`${WIKI_API_BASE_URL}/${id}`, page);
  }

  deletePage(id) {
    return axios.delete(`${WIKI_API_BASE_URL}/${id}`);
  }
}

export default new WikiService();
