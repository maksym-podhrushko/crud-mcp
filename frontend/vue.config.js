const { defineConfig } = require("@vue/cli-service");
module.exports = defineConfig({
  transpileDependencies: true,
  devServer: {
    port: 8888,
    proxy: {
      "/api": {
        target: "http://localhost:8080", // Your Spring Boot server address
        changeOrigin: true,
      },
    },
  },
});
