
# Multi-stage Dockerfile for building the Vite + React (TypeScript) frontend
# Stage 1: build the app with Node
FROM node:18-alpine AS builder

WORKDIR /app

# Copy package manifests first to install deps (better cache behavior)
COPY package*.json ./

# Install dependencies (dev deps needed for the build)
RUN npm ci

# Copy source
COPY . .

# Build the production assets
RUN npm run build

# Stage 2: serve the built assets with nginx
FROM nginx:stable-alpine AS runner

# Remove default nginx config and add SPA-friendly config
COPY nginx.conf /etc/nginx/conf.d/default.conf

# Copy built static files from builder
COPY --from=builder /app/dist /usr/share/nginx/html

EXPOSE 80

CMD ["nginx", "-g", "daemon off;"]

