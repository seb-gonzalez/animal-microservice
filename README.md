
# Animal Image Microservice

A lightweight Spring Boot microservice for capturing and retrieving animal images (cats, dogs, bears) from third-party APIs, with persistent storage using an embedded H2 database.

## Overview

This microservice provides two REST endpoints:

- **`POST /api/animals/capture`**: Captures a specified number of images for a given animal type and stores them in the database.
- **`GET /api/animals/{animalType}/latest`**: Retrieves the most recently stored image for a specified animal type.

Supported animal types and their image sources:

- **Cats**: `https://placekitten.com/200/300`
- **Dogs**: `https://place.dog/200/300`
- **Bears**: `https://placebear.com/200/300`

Data is persisted in a file-based H2 database (`animaldb.mv.db`), either in the project root (local) or a Docker volume (containerized).

## Prerequisites

- **Java**: 8 or higher (tested with 11; adjust `pom.xml` for newer versions if needed).
- **Maven**: 3.6+ for dependency management and building.
- **IDE**: Eclipse (or any Java IDE with Maven support).
- **curl** or **Postman**: For testing API endpoints.
- **Docker**: For containerized deployment (optional).
- **Docker Compose**: For multi-container setup (optional).

## Setup

### 1. Clone the Project:

```bash
git clone <repository-url>
cd animal-microservice
```

### 2. Install Dependencies:

- Open in Eclipse: `File > Import > Maven > Existing Maven Projects`.
- Right-click project → `Maven > Update Project`.

### 3. Run Locally:

- Right-click `src/main/java/com/challenge/AnimalMicroserviceApplication.java` → `Run As > Java Application`.
- The application starts on `http://localhost:8080`.

## Project Structure

```
animal-microservice/
├── src/
│   ├── main/
│   │   ├── java/
│   │   │   └── com/challenge/
│   │   │       ├── AnimalMicroserviceApplication.java  # Main entry point
│   │   │       ├── controller/
│   │   │       │   └── AnimalController.java          # REST controller
│   │   │       ├── model/
│   │   │       │   ├── CaptureRequest.java            # Request DTO for POST
│   │   │       │   └── ImageResponse.java             # JPA entity for images
│   │   │       └── repository/
│   │   │           └── ImageRepository.java           # JPA repository
│   │   └── resources/
│   │       └── application.properties                 # H2 config
│   └── test/                                          # (Empty for now)
├── Dockerfile                                         # Docker configuration
├── docker-compose.yml                                 # Docker Compose configuration
├── pom.xml                                            # Maven config
└── README.md                                          # This file
```

## Configuration

### `pom.xml`

Uses Spring Boot 3.2.3 with spring-boot-starter-web, spring-boot-starter-data-jpa, and h2 dependencies.

### `application.properties`

```properties
spring.datasource.url=jdbc:h2:file:./animaldb
spring.datasource.driver-class-name=org.h2.Driver
spring.datasource.username=sa
spring.datasource.password=
spring.jpa.database-platform=org.hibernate.dialect.H2Dialect
spring.jpa.hibernate.ddl-auto=update
spring.jpa.show-sql=true
```

Stores data in `./animaldb.mv.db` (local) or a mounted volume (Docker).

## Running with Docker

You can containerize and run the application using Docker and Docker Compose.

### Prerequisites

- Docker and Docker Compose installed.

### `Dockerfile`

Create a `Dockerfile` in the project root:

```dockerfile
# Build stage
FROM maven:3.8.6-openjdk-11 AS build
WORKDIR /app
COPY pom.xml .
COPY src ./src
RUN mvn clean package -DskipTests

# Run stage
FROM openjdk:11-jre-slim
WORKDIR /app
COPY --from=build /app/target/animal-microservice-1.0-SNAPSHOT.jar app.jar
EXPOSE 8080
ENTRYPOINT ["java", "-jar", "app.jar"]
```

### `docker-compose.yml`

Create a `docker-compose.yml` in the project root:

```yaml
version: '3.8'
services:
  animal-service:
    build: .
    ports:
      - "8080:8080"
    volumes:
      - h2-data:/app
    environment:
      - SPRING_DATASOURCE_URL=jdbc:h2:file:/app/animaldb
volumes:
  h2-data:
```

### Steps

#### Build and Run:

```bash
docker-compose up --build
```

- Builds the Docker image and starts the container.
- Accessible at `http://localhost:8080`.
- H2 database persists in the `h2-data` volume.

#### Stop:

```bash
docker-compose down
```

- Stops the container; data remains in the volume.

#### Stop and Remove Volume (if resetting data):

```bash
docker-compose down -v
```

## Usage

### Capture Images

- **Endpoint**: `POST /api/animals/capture`

Request (Local or Docker):

```bash
curl -X POST http://localhost:8080/api/animals/capture \
  -H "Content-Type: application/json" \
  -d '{"animal": "cats", "count": 2}'
```

Response:

```json
{
  "message": "Successfully captured 2 cats images",
  "images": [
    {"id": "1", "url": "https://placekitten.com/200/300?v=1677654321000", "stored_at": "2025-03-19T12:00:00Z"},
    {"id": "2", "url": "https://placekitten.com/200/300?v=1677654321001", "stored_at": "2025-03-19T12:00:01Z"}
  ]
}
```

### Get Latest Image

- **Endpoint**: `GET /api/animals/{animalType}/latest`

Request (Local or Docker):

```bash
curl http://localhost:8080/api/animals/cats/latest
```

Response:

```json
{
  "animal": "cats",
  "id": "2",
  "url": "https://placekitten.com/200/300?v=1677654321001",
  "stored_at": "2025-03-19T12:00:01Z"
}
```

## Database

- **H2**: Embedded, file-based database.
- **Schema**: Auto-generated from `ImageResponse` entity.
- **Table**: `IMAGE_RESPONSE`
  - **Columns**: `ID (Long, auto-increment)`, `ANIMAL (String)`, `URL (String)`, `STORED_AT (String)`.

Access:

- Add `spring.h2.console.enabled=true` to `application.properties` and visit `http://localhost:8080/h2-console` (JDBC URL: `jdbc:h2:file:./animaldb` for local, or `/app/animaldb` in Docker).

## Development Notes

- **Persistence**: Switched from in-memory `Map` to H2 for durability across restarts.
- **Animal Types**: Restricted to cats, dogs, bears with placeholder APIs.
- **Error Handling**: Basic validation for animal type and count; extend as needed.

## Troubleshooting

- **BeanCreationException**: Ensure `ImageResponse` has `@Entity` and `@Id` annotations.
- **Type Mismatch**: Response maps use `HashMap<String, Object>` for flexibility.
- **Logs**: Check console output with `spring.jpa.show-sql=true` for SQL debugging.

## Future Enhancements

- Add API authentication (e.g., JWT).
- Support more animal types or dynamic APIs.
- Optimize `findByAnimal` with a sorted query instead of in-memory sorting.
```

This markdown README contains everything, including configuration, Docker setup, usage instructions, and more. You can paste this into your `README.md` file and have the full documentation for your project. Let me know if you need any more adjustments!
