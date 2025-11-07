
# Multi-stage Dockerfile for building the Vite + React (TypeScript) frontend
# Stage 1: build the app with Node
FROM node:18-alpine AS builder
WORKDIR /app
COPY package*.json ./
RUN npm ci
COPY . .
ARG VITE_FLIGHT_API_URL
# Create a small .env file for Vite at build time so the built assets use the correct API base URL.
# Default to localhost if the build arg is not provided (useful for local dev). This file will be
# included in the build context inside the container before `npm run build`.
RUN /bin/sh -c 'printf "VITE_FLIGHT_API_URL=%s\n" "${VITE_FLIGHT_API_URL:-http://localhost:8080/api/v1/flights}" > .env'
RUN npm run build

# Stage 2: serve the built assets with nginx
FROM nginx:stable-alpine AS runner
COPY nginx.conf /etc/nginx/conf.d/default.conf
COPY --from=builder /app/dist /usr/share/nginx/html
EXPOSE 5173
CMD ["nginx", "-g", "daemon off;"]

