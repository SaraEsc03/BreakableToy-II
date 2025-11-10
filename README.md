# BreakableToy-II: Flights Search
BreakableToy-II is a full-stack flight search application built for learning and experimentation purposes. It allows users to **search for flights, view round-trip and sigle-trip options, and explore detailed flight information** using the Amadeus REST API.
This project was built using Spring Boot, Gradle (backend), React + Typescript (frontend) and Docker Compose for containarize both parts of the application

## Features
  - Fully responsive design for desktop and large screens.
  - Search for flights by origin, destination, dates, and passengers.
  - Supports one-way and round-trip search.
  - Autocomplete suggestions for airports.
  - Displays flight prices, airlines, and durations.
  - Detailed view for each flight offer.

## Getting Started
### Prerequisites
  - Node.js (v18+)
  - npm (v9+)
  - Java 17+
  - Gradle
  - Docker Desktop (Personal license)

## Running the App
## 1. Running Manually
### Backend:
```
cd backend
./gradlew bootRun
```
The backend will start at http://localhost:8080/

### Frontend:
```
cd frontend
npm install
npm run dev
```
The frontend will start at http://localhost:5173/

## 2. Running on Docker
```
docker compose up --build
```
The frontend will start at http://localhost:5173/
The backend will start at http://localhost:8080/

## API Documentation
**Base URL**
  - Frontend: http://localhost:5173
  - Backend: http://localhost:8080/api/v1

### Authentication
  - The backend talks to the Amadeus API using server-side credentials. The public API exposed to the frontend does not require a client API key.
  - Required backend environment variables (set in .env or your environment):
      - AMADEUS_API_KEY
      - AMDEUS_API_SECRET
  - The backend caches Amadeus access tokens; it does not request a new token for every call.

### Endpoints
#### 1. Airport Autocomplete: return a short list of airport suggestions for typeahead/autocomplete.
**GET /api/v1/search**
**Query parameters**:
  - g (string, required): keyword for searching
  - limit (int, optional): maximum number of results (default 10)

#### 2. Flight search: search flight offers for given requirements
**GET /api/v1/flights/search**
**Query parameters**:
  - origin (string, required): IATA code of origin
  - destination (string, required): IATA code of destination
  - departureDate (string, required): departure date. Backend only accepts YYY-MM-DD
  - returnDate (string, required): return date date. Backend only accepts YYY-MM-DD
  - adults (integer, default 1): number of adult travelers
  - nonStop (boolean, optional): limit results to non-stop flights
  - currencyCode (string, optional): currency for prices
