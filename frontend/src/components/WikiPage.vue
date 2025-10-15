<template>
  <div class="container">
    <!-- Title row with animated add button -->
    <div class="title-row">
      <h1 class="m-0">Wiki Articles</h1>
      <button
        class="add-button"
        @click="toggleCreateModal"
        aria-label="Add new wiki article"
      >
        <svg
          xmlns="http://www.w3.org/2000/svg"
          width="20"
          height="20"
          fill="currentColor"
          viewBox="0 0 16 16"
        >
          <path
            d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z"
          />
        </svg>
      </button>
    </div>

    <!-- Full-width cards, one per line -->
    <div class="row">
      <div class="col-12 mb-4" v-for="page in pages" v-bind:key="page.id">
        <div class="card shadow-sm">
          <div class="row g-0">
            <!-- Image placeholder (1/3 width) -->
            <div class="col-md-4">
              <div v-if="page.imageUrl" class="image-container">
                <img
                  :src="page.imageUrl"
                  :alt="page.title"
                  class="wiki-image"
                  @load="onImageLoad"
                  @error="onImageError"
                />
              </div>
              <div v-else class="image-placeholder">
                <span>Image Placeholder</span>
              </div>
            </div>

            <!-- Content section (2/3 width) -->
            <div class="col-md-8">
              <div
                class="card-header d-flex justify-content-between align-items-center"
              >
                <div>
                  <h5 class="card-title mb-0">{{ page.title }}</h5>
                  <div class="badge bg-secondary">ID: {{ page.id }}</div>
                </div>
                <!-- Show more/less button moved to header -->
                <button
                  class="btn btn-link"
                  @click="toggleRow(page.id)"
                  type="button"
                >
                  {{ expandedRows[page.id] ? "Show less" : "Show more" }}
                </button>
              </div>

              <div class="card-body">
                <p class="card-text">
                  <span v-if="!expandedRows[page.id]">
                    {{
                      page.content.length > 200
                        ? page.content.slice(0, 800) + "..."
                        : page.content
                    }}
                  </span>
                  <span v-else>
                    {{ page.content }}
                  </span>
                </p>
              </div>

              <div class="card-footer">
                <div class="d-flex justify-content-end">
                  <button
                    class="btn btn-warning btn-sm me-2"
                    @click="startEdit(page)"
                  >
                    Update
                  </button>
                  <button
                    class="btn btn-danger btn-sm"
                    @click="deletePage(page.id)"
                  >
                    Delete
                  </button>
                </div>
              </div>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Expanded article view -->
    <div v-if="expandedArticle" class="expanded-article">
      <div class="card shadow">
        <div
          class="card-header d-flex justify-content-between align-items-center"
        >
          <h4>{{ expandedArticle.title }}</h4>
          <button class="btn-close" @click="closeExpandedArticle"></button>
        </div>
        <div class="card-body">
          <div class="row">
            <div class="col-md-4 mb-4">
              <div v-if="expandArticle.imageUrl" class="image-container large">
                <img
                  :src="expandArticle.imageUrl"
                  :alt="expandedArticle.title"
                  class="wiki-image"
                />
              </div>
              <div class="image-placeholder large">
                <span>Image Placeholder</span>
              </div>
            </div>
            <div class="col-md-8">
              <p>{{ expandedArticle.content }}</p>
            </div>
          </div>
        </div>
      </div>
    </div>

    <!-- Edit form Modal-->
    <div class="page-modal" v-if="showEditModal">
      <div class="modal-backdrop" @click="toggleEditModal"></div>
      <div class="modal-content">
        <div class="card">
          <div
            class="card-header bg-primary text-white d-flex justify-content-between align-items-center"
          >
            <h5 class="mb-0">Edit Page</h5>
            <button class="btn-close-modal" @click="toggleEditModal">×</button>
          </div>
          <div class="card-body">
            <form @submit.prevent="submitEdit">
              <div class="mb-3">
                <label for="editTitle" class="form-label">Title</label>
                <input
                  id="editTitle"
                  v-model="editForm.title"
                  class="form-control"
                  type="text"
                  placeholder="Title"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="editContent" class="form-label">Content</label>
                <textarea
                  id="editContent"
                  v-model="editForm.content"
                  class="form-control"
                  placeholder="Content"
                  rows="5"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="editImageUrl" class="form-label">Image URL</label>
                <input
                  id="editImageUrl"
                  v-model="editForm.imageUrl"
                  class="form-control"
                  type="text"
                  placeholder="Image URL"
                />
              </div>
              <div class="d-flex gap-2">
                <button class="btn btn-primary" :disabled="submitting">
                  {{ submitting ? "Updating..." : "Update Page" }}
                </button>
                <button
                  type="button"
                  class="btn btn-secondary"
                  @click="cancelEdit"
                >
                  Close
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Create Page Modal -->
    <div class="page-modal" v-if="showCreateModal">
      <div class="modal-backdrop" @click="toggleCreateModal"></div>
      <div class="modal-content">
        <div class="card">
          <div
            class="card-header bg-primary text-white d-flex justify-content-between align-items-center"
          >
            <h5 class="mb-0">Create New Page</h5>
            <button class="btn-close-modal" @click="toggleCreateModal">
              ×
            </button>
          </div>
          <div class="card-body">
            <form @submit.prevent="createPage">
              <div class="mb-3">
                <label for="pageTitle" class="form-label">Title</label>
                <input
                  type="text"
                  class="form-control"
                  id="pageTitle"
                  v-model="newPage.title"
                  required
                />
              </div>
              <div class="mb-3">
                <label for="pageContent" class="form-label">Content</label>
                <textarea
                  class="form-control"
                  id="pageContent"
                  rows="4"
                  v-model="newPage.content"
                  required
                ></textarea>
              </div>
              <div class="mb-3">
                <label for="pageImageUrl" class="form-label">Image URL</label>
                <input
                  type="text"
                  class="form-control"
                  id="pageImageUrl"
                  v-model="newPage.imageUrl"
                  placeholder="Image URL"
                />
              </div>
              <div class="d-flex justify-content-between">
                <button
                  type="button"
                  class="btn btn-secondary"
                  @click="toggleCreateModal"
                >
                  Cancel
                </button>
                <button
                  type="submit"
                  class="btn btn-primary"
                  :disabled="submitting"
                >
                  {{ submitting ? "Creating..." : "Create Page" }}
                </button>
              </div>
            </form>
          </div>
        </div>
      </div>
    </div>

    <!-- Floating Action Button -->
    <div class="floating-action-btn" @click="showCreateModal = true">
      <svg
        xmlns="http://www.w3.org/2000/svg"
        fill="currentColor"
        viewBox="0 0 16 16"
      >
        <path
          d="M8 2a.5.5 0 0 1 .5.5v5h5a.5.5 0 0 1 0 1h-5v5a.5.5 0 0 1-1 0v-5h-5a.5.5 0 0 1 0-1h5v-5A.5.5 0 0 1 8 2Z"
        />
      </svg>
    </div>
  </div>
</template>

<script>
import WikiService from "@/services/WikiService";

export default {
  name: "WikiPage",
  data() {
    return {
      pages: [],
      newPage: { title: "", content: "" },
      editPage: null,
      editForm: { title: "", content: "" },
      submitting: false,
      createError: null,
      loading: false,
      error: null,
      expandedRows: {},
      isEditing: false,
      expandedArticle: null,
      showCreateModal: false, // Control visibility of create modal
      showEditModal: false,
    };
  },
  methods: {
    async fetchFromWikipedia(title) {
      if (!title || !title.trim()) {
        // Show an error if no title is provided
        this.createError = "Please enter a Wikipedia page title";
        return;
      }

      this.submitting = true;
      this.createError = null;

      try {
        // Call the API to fetch from Wikipedia
        await WikiService.fetchFromWikipedia({ title });

        // After successful fetch, refresh the page list to show the new entry
        await this.getPages();
      } catch (error) {
        console.error("Error fetching from Wikipedia:", error);
        this.createError = "Failed to fetch page from Wikipedia";
      } finally {
        this.submitting = false;
      }
    },

    async getPages() {
      this.loading = true;
      this.error = null;
      try {
        const { data } = await WikiService.getPages();
        this.pages = data;
      } catch (error) {
        this.error = "Failed to load pages";
        console.error(error);
      } finally {
        this.loading = false;
      }
    },

    async deletePage(id) {
      try {
        await WikiService.deletePage(id);
        await this.getPages(); // pull fresh list
      } catch (error) {
        console.error("Failed to delete page", error);
      }
    },

    toggleRow(id) {
      this.expandedRows = {
        ...this.expandedRows,
        [id]: !this.expandedRows[id],
      };
    },

    expandArticle(page) {
      this.expandedArticle = page;
      // Scroll to top to show the expanded article
      window.scrollTo({ top: 0, behavior: "smooth" });
    },

    closeExpandedArticle() {
      this.expandedArticle = null;
    },

    startEdit(page) {
      this.toggleEditModal(page);
    },

    async submitEdit() {
      if (!this.editPage) return;
      this.submitting = true;
      try {
        await WikiService.updatePage(this.editPage.id, this.editForm);
        this.showEditModal = false; // Close the modal after successful update
        await this.getPages();
      } catch (error) {
        console.error("Failed to update page", error);
      } finally {
        this.submitting = false;
        await this.getPages();
      }
    },

    cancelEdit() {
      this.editPage = null;
      this.editForm = { title: "", content: "" };
      this.isEditing = false;
    },

    async createPage() {
      this.submitting = true;
      this.createError = null;

      try {
        const page = {
          title: this.newPage.title,
          content: this.newPage.content,
          imageUrl: this.newPage.imageUrl || null,
        };

        await WikiService.createPage(page);
        await this.getPages();

        this.newPage.title = "";
        this.newPage.content = "";
        this.newPage.imageUrl = "";
        this.showCreateModal = false;
      } catch (error) {
        this.createError = "Failed to create page.";
        console.error(error);
      } finally {
        this.submitting = false;
      }
    },

    toggleCreateModal() {
      this.showCreateModal = !this.showCreateModal;
      if (!this.showCreateModal) {
        // Reset form when closing
        this.newPage = { title: "", content: "", imageUrl: "" };
      }
    },

    toggleEditModal(page) {
      this.showEditModal = !this.showEditModal;
      if (this.showEditModal && page) {
        this.editPage = page;
        this.editForm.title = page.title;
        this.editForm.content = page.content;
        this.editForm.imageUrl = page.imageUrl || "";
      } else {
        // Reset form when closing
        this.editPage = null;
        this.editForm = { title: "", content: "", imageUrl: "" };
      }
    },

    handleWikiPageAdded(pageId) {
      console.log("Wiki page added:", pageId);
      // Refresh the pages
      this.getPages();
    },
  },

  mounted() {
    // Load wiki pages
    this.getPages();

    // Listen for real-time updates
    this.emitter.on("notify-wiki", this.handleWikiPageAdded);
  },
  beforeUnmount() {
    // Clean up event listener
    this.emitter.off("notify-wiki", this.handleWikiPageAdded);
  },

  created() {
    this.getPages();
  },
};
</script>

<style scoped>
/* Title row container */
.title-row {
  position: relative;
  display: inline-flex;
  align-items: center;
  margin-bottom: 1.5rem;
  padding: 0.25rem 0.5rem;
  border-radius: 0.375rem;
}

/* Heading style */
.title-row h1 {
  font-weight: 600;
  margin: 0;
  transition: transform 0.2s ease;
}

/* Add button styling */
.add-button {
  background: none;
  border: none;
  padding: 0.25rem;
  margin-left: 0.5rem;
  width: 1.75rem;
  height: 1.75rem;
  display: inline-flex;
  align-items: center;
  justify-content: center;
  cursor: pointer;
  border-radius: 0.25rem;
  transform: translateX(-5px);
  opacity: 0;
  transition: all 0.2s cubic-bezier(0.3, 0, 0.5, 1);
}

/* Show button on hover */
.title-row:hover .add-button {
  opacity: 0.8;
  transform: translateX(0);
}

/* Button hover state */
.add-button:hover {
  background-color: rgba(55, 53, 47, 0.08);
  opacity: 1;
}

/* Button active state */
.add-button:active {
  background-color: rgba(55, 53, 47, 0.16);
  transform: scale(0.92);
}

/* SVG icon styling */
.add-button svg {
  color: #37352f;
  transition: transform 0.2s ease;
}

.add-button:hover svg {
  transform: rotate(180deg);
  color: #0071e3;
}

.card {
  border: none;
  border-radius: 8px;
  transition: all 0.2s ease;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.05) !important;
  overflow: hidden;
}

.card:hover {
  box-shadow: 0 8px 15px rgba(0, 0, 0, 0.1) !important;
}

.card-header {
  background-color: #ffffff;
  border-bottom: 1px solid rgba(0, 0, 0, 0.05);
  padding: 1.25rem 1.25rem 0.75rem;
  position: relative;
  text-align: left;
}

.card-header h5 {
  font-weight: 600;
  color: #333;
  margin-bottom: 0.5rem;
  text-align: left;
}

.card-body {
  padding: 1.25rem;
  text-align: left;
}

.card-text {
  color: #555;
  font-size: 0.95rem;
  line-height: 1.6;
  text-align: left;
}

.card-footer {
  background-color: #ffffff;
  border-top: 1px solid rgba(0, 0, 0, 0.05);
  padding: 0.75rem 1.25rem;
  text-align: left;
}

/* Image placeholder */
.image-placeholder {
  background-color: #f8f9fa;
  border: 1px dashed #dee2e6;
  height: 100%;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  color: #adb5bd;
  font-size: 0.9rem;
}

/* Image container styling */
.image-container {
  height: 100%;
  min-height: 200px;
  display: flex;
  align-items: center;
  justify-content: center;
  overflow: hidden;
  background-color: #f8f9fa;
  border-radius: 4px;
}

.image-container.large {
  min-height: 300px;
}

/* Actual image styling */
.wiki-image {
  width: 100%;
  height: 100%;
  object-fit: cover;
  border-radius: 4px;
  transition: transform 0.2s ease;
}

.wiki-image:hover {
  transform: scale(1.02);
}

/* Responsive image adjustments */
@media (max-width: 768px) {
  .image-container {
    min-height: 150px;
  }

  .image-container.large {
    min-height: 200px;
  }
}

/* Button styling */
.btn-link {
  color: #0071c2;
  font-size: 0.85rem;
  padding: 0;
  text-decoration: none;
  text-align: left;
}

.btn-link:hover {
  color: #00487b;
  text-decoration: underline;
}

/* Neutral buttons with consistent styling */
.btn {
  border-radius: 4px;
  padding: 6px 15px;
  font-size: 0.9rem;
  font-weight: 500;
  transition: background-color 0.2s ease;
  border: 1px solid #e7e7e7;
}

.btn-primary {
  background-color: #0071c2;
  border-color: #0071c2;
  color: white;
}

.btn-primary:hover {
  background-color: #00487b;
  border-color: #00487b;
}

.btn-warning {
  background-color: #0f47e0;
  border-color: #e7e7e7;
  color: #ffffff;
}

.btn-warning:hover {
  background-color: #35045e;
  border-color: #d9d9d9;
  color: #ffffff;
}

.btn-danger {
  background-color: #ffffff;
  border-color: #0672ff;
  color: #333333;
}

.btn-danger:hover {
  background-color: #f5f5f5;
  border-color: #d9d9d9;
  color: #333333;
}

.btn-success {
  background-color: #0071c2;
  border-color: #0071c2;
  color: white;
}

.btn-success:hover {
  background-color: #00487b;
  border-color: #00487b;
}

/* Badge styling */
.badge {
  background-color: #ecf0f1 !important;
  color: #7f8c8d !important;
  font-weight: 500;
  font-size: 0.7rem;
  border-radius: 4px;
  margin-left: 8px;
}

/* Form styling */
.form-control {
  border: 1px solid #e0e0e0;
  border-radius: 4px;
  padding: 0.6rem 0.75rem;
  transition: border-color 0.15s ease-in-out, box-shadow 0.15s ease-in-out;
}

.form-control:focus {
  border-color: #3498db;
  box-shadow: 0 0 0 0.2rem rgba(52, 152, 219, 0.25);
}

textarea.form-control {
  min-height: 100px;
}

/* Expanded article styling */
.expanded-article {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(255, 255, 255, 0.95);
  z-index: 1000;
  padding: 2rem;
  overflow-y: auto;
  animation: fadeIn 0.3s ease-out forwards;
}

@keyframes fadeIn {
  from {
    opacity: 0;
  }
  to {
    opacity: 1;
  }
}

.expanded-article .card {
  max-width: 1200px;
  margin: 0 auto;
  box-shadow: 0 10px 30px rgba(0, 0, 0, 0.1) !important;
}

.expanded-article .card-header {
  padding: 1.5rem;
}

.expanded-article .card-body {
  padding: 2rem;
}

/* Animation for edit/create forms */
@keyframes slideDown {
  from {
    opacity: 0;
    transform: translateY(-10px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

.mt-4 {
  animation: slideDown 0.3s ease-out forwards;
}

/* Floating action button */
.floating-action-btn {
  position: fixed;
  bottom: 30px;
  right: 30px;
  width: 60px;
  height: 60px;
  border-radius: 50%;
  background-color: #0071c2;
  color: white;
  display: flex;
  align-items: center;
  justify-content: center;
  box-shadow: 0 4px 10px rgba(0, 0, 0, 0.2);
  cursor: pointer;
  z-index: 900;
  transition: all 0.2s ease;
}

.floating-action-btn svg {
  width: 30px;
  height: 30px;
}

.floating-action-btn:hover {
  background-color: #00487b;
  transform: scale(1.05);
}

/* Modal styles */
.page-modal {
  position: fixed;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  display: flex;
  align-items: center;
  justify-content: center;
  z-index: 1050;
}

.modal-backdrop {
  position: absolute;
  top: 0;
  left: 0;
  right: 0;
  bottom: 0;
  background-color: rgba(0, 0, 0, 0.5);
  backdrop-filter: blur(3px);
}

.modal-content {
  position: relative;
  width: 90%;
  max-width: 1000px;
  z-index: 1051;
  animation: modal-in 0.3s ease-out;
}

.btn-close-modal {
  background: transparent;
  border: none;
  font-size: 24px;
  font-weight: bold;
  color: white;
  cursor: pointer;
  line-height: 1;
}

@keyframes modal-in {
  from {
    opacity: 0;
    transform: translateY(20px);
  }
  to {
    opacity: 1;
    transform: translateY(0);
  }
}

/* Responsive adjustments */
@media (max-width: 768px) {
  .card-header h5 {
    font-size: 1.1rem;
  }

  .card-text {
    font-size: 0.9rem;
  }

  .image-placeholder {
    min-height: 150px;
  }
}
</style>
