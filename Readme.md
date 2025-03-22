
# Animal Image Microservice

A Spring Boot microservice that captures and retrieves animal images (cats, dogs, bears) from third-party APIs, storing them in an embedded H2 database.

## Features
- **POST `/api/animals/capture`**: Capture specified number of animal images and store in the database.
- **GET `/api/animals/{animalType}/latest`**: Retrieve the latest stored image for a given animal type.

Supported animal types: Cats, Dogs, Bears.

## Setup

1. **Clone the Project**:

```bash
git clone <repository-url>
cd animal-microservice
```

2. **Run Locally**:
   - Open in your IDE and run `AnimalMicroserviceApplication.java`.
   - Access app at `http://localhost:8080`.

## Configuration

Uses an H2 database for storing images. Modify `application.properties` for custom database configurations.

## Running with Docker

1. **Build and Run**:

```bash
docker-compose up --build
```

2. **Stop**:

```bash
docker-compose down
```

3. **Stop and Remove Volume**:

```bash
docker-compose down -v
```

## API Usage

- **Capture Images**:  
  `POST /api/animals/capture`  
  Example Request:
  ```bash
  curl -X POST http://localhost:8080/api/animals/capture -d '{"animal": "cats", "count": 2}'
  ```

- **Get Latest Image**:  
  `GET /api/animals/{animalType}/latest`  
  Example Request:
  ```bash
  curl http://localhost:8080/api/animals/cats/latest
  ```

## Database

Uses an embedded H2 database for persistence, with data stored in `animaldb.mv.db`.

## Troubleshooting

- Ensure correct annotations (`@Entity`, `@Id`) on JPA entities.
- Enable SQL logs by setting `spring.jpa.show-sql=true`.

## Future Enhancements
- API authentication (e.g., JWT).
- Support for more animal types.
- Optimized querying for images.
