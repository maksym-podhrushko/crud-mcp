FROM node:22-alpine as builder

WORKDIR /app

COPY package*.json ./

RUN npm install

COPY . .

# Accept build argument
ARG VUE_APP_WIKI_API_BASE_URL
ENV VUE_APP_WIKI_API_BASE_URL=$VUE_APP_WIKI_API_BASE_URL

RUN npm run build

FROM nginx:alpine

COPY --from=builder /app/dist /usr/share/nginx/html
COPY nginx.conf /etc/nginx/nginx.conf

# Ensure nginx runs in foreground
EXPOSE 8888

# Run nginx in foreground (not as daemon)
CMD ["nginx", "-g", "daemon off;"]
