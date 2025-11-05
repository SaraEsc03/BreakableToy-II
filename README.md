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
