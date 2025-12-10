# TedTalks API

## Table of Contents
- [Overview](#overview)
- [Features](#features)
- [Technology Stack](#technology-stack)
- [Architecture](#architecture)
- [Database Design](#database-design)
- [Local Development](#local-development)
- [API Documentation](#api-documentation)
- [Deployment](#deployment)

## Overview
The **TedTalks API** is a RESTful service designed to manage a repository of Ted Talks and analyze their impact. It processes a dataset of talks, calculating an "influence score" for each based on user engagement metrics (views and likes). The system allows for searching, CRUD operations, and analytical queries to identify the most influential speakers and talks over time.

## Features
| Feature             | Description                                                 |
|---------------------|-------------------------------------------------------------|
| Data Import         | Automatically imports `iO Data - Java assessment.csv` on startup. |
| Influence Scaling   | Calculates `Influence` = `Views + (2 * Likes)`.             |
| Search              | Search talks by `title` or `author` (case-insensitive).     |
| Top Speakers        | Identify top speakers based on average influence rating (1-5 stars). |
| Talk of the Year    | Find the single most influential talk for each year.        |
| Data Persistence    | Stores all records in an in-memory H2 database for quick setup. |

## Technology Stack
| Component         | Technology                  |
|-------------------|-----------------------------|
| Framework         | Spring Boot 3 (Java 21)     |
| Database          | H2 In-Memory Database       |
| Persistence       | Spring Data JPA / Hibernate |
| Build Tool        | Maven                       |
| Validation        | Bean Validation (Hibernate Validator) |
| Testing           | JUnit 5, Mockito            |

## Architecture

### Design Patterns
- **Command Pattern**: Write operations (`create`, `update`, `delete`) are encapsulated as self-executing Command objects.
- **Service Layer**: Handles read-only business logic
- **Repository Pattern**: Abstraction over data access using Spring Data JPA.
- **Global Exception Handling**: Centralized error management returning standardized `ApiResponse`.

## Database Design
#### Tables Overview
| Table Name           | Purpose             | Key Fields                            |
|----------------------|---------------------|---------------------------------------|
| ted_talks_record_t   | Main TedTalk records| `ttr_id` (UUID), `title`, `author`, `views`, `likes`, `influence_score` |

### Database Tables

#### ted_talks_record_t
- **ttr_id**: Primary Key, UUID.
- **title**: Title of the talk (max 200 chars).
- **author**: Speaker name (max 100 chars).
- **released_date**: Date the talk was published.
- **view_count**: Total views (non-negative).
- **like_count**: Total likes (non-negative).
- **influence_score**: Calculated metric for analysis.
- **url**: Link to the talk.

## Local Development

### Prerequisites
- Java 21+
- Maven (Wrapper provided)

### How to Run
1. **Clone and Build**:
   ```sh
   ./mvnw clean install
   ```
2. ### Running the Application

#### Profiles
The application supports the following profiles:
- **`local`** (Default): Uses H2 in-memory database.
- **`dev`**: Uses PostgreSQL and requires environment variables.

**Run with Default (Local) Profile**:
```shell
./mvnw spring-boot:run
```

**Run with Dev Profile**:
```shell
export DB_URL=jdbc:postgresql://localhost:5432/tedtalks
export DB_USERNAME=postgres
export DB_PASSWORD=******
./mvnw spring-boot:run -Dspring-boot.run.profiles=dev
```

### Accessing the API
   *Alternatively, run the JAR directly:*
   ```sh
   java -jar target/tedtalks-api-0.0.1-SNAPSHOT.jar
   ```
   The application will start on **port 8080**.

### How to Test
Run the full test suite with:
```sh
./mvnw clean test
```


### Assumptions Made
1. **H2 Database**: Used for ease of local testing and assessment. PostgreSQL credentials exist in config but are disabled.
2. **Influence Formula**: Influence is rigidly defined as `Views + (2 * Likes)`.
3. **Future Dates**: Records with future release dates are considered pre-release and must have `0` views and `0` likes. The import process enforces this.
4. **CSV Import**: The application expects `iO Data - Java assessment.csv` in `src/main/resources`.
5. **Invalid/pre-TED year**: If any TedTalk has been made before 1984, the row is skipped.

## API Documentation

### Endpoints

#### 1. Search Talks
Search for talks by title or author.
- **URL**: `/tedtalks/search`
- **Method**: `GET`
- **Query Params**: `value` (string)

**Example Request**:
```shell
curl --location 'http://localhost:8080/tedtalks/search?value=climate'
```

#### 2. Create TedTalk
Add a new talk to the system.
- **URL**: `/tedtalks`
- **Method**: `POST`
- **Body**: JSON

**Example Request**:
```shell
curl --location 'http://localhost:8080/tedtalks' \
--header 'Content-Type: application/json' \
--data '{
    "title": "The future of AI",
    "author": "John Doe",
    "date": "2024-01-01",
    "views": 1000,
    "likes": 500,
    "link": "https://ted.com/talks/example"
}'
```

#### 3. Update TedTalk
Update an existing talk.
- **URL**: `/tedtalks/{id}`
- **Method**: `PUT`

**Example Request**:
```shell
curl --location 'http://localhost:8080/tedtalks/123e4567-e89b-12d3-a456-426614174000' \
--header 'Content-Type: application/json' \
--data '{
    "title": "The future of AI (Updated)",
    "author": "John Doe",
    "date": "2024-01-01",
    "views": 1500,
    "likes": 600,
    "link": "https://ted.com/talks/example"
}'
```

#### 4. Delete TedTalk
Remove a talk.
- **URL**: `/tedtalks/{id}`
- **Method**: `DELETE`

**Example Request**:
```shell
curl --location --request DELETE 'http://localhost:8080/tedtalks/123e4567-e89b-12d3-a456-426614174000'
```

#### 5. Get Top Influential Speakers
Get a list of speakers with high average influence ratings (1-5 stars).
- **URL**: `/tedtalks/speaker/influential`
- **Method**: `GET`
- **Params**: `limit` (default 10), `minTalks` (default 1)

**Example Request**:
```shell
curl --location 'http://localhost:8080/tedtalks/speaker/influential?limit=5'
```

#### 6. Get Top Talk Per Year
Find the single most influential talk for every year in the dataset.
- **URL**: `/tedtalks/influence/year`
- **Method**: `GET`

**Example Request**:
```shell
curl --location 'http://localhost:8080/tedtalks/influence/year'
```

## Deployment
This application is packaged as a standard JAR file.
```sh
java -jar target/tedtalks-api-0.0.1-SNAPSHOT.jar
```
*Note: Ensure `application.yaml` is configured for the target environment (e.g., switching to a persistent PostgreSQL database for production).*
