# Product Service

Microservice for product management in the stock system. Built with Spring Boot + WebFlux, JWT security, Kafka + Avro, MongoDB, and Redis.

---

## 🔄 Technologies

- Java 21
- Spring Boot 3
- Spring WebFlux
- Spring Security (JWT)
- Kafka + Avro + Schema Registry
- MongoDB Reactive
- Redis Reactive
- Docker Compose
- Testcontainers for testing
- OpenAPI 3 (Swagger UI)
- MapStruct

See more in the **[Technology Justification Page](docs/technologies.md)**

---

## 📚 Project Architecture
<img src="docs/architecture/ArchitectureComponent.png" alt="Component">

---

## 🚀 Key Features

- Create, retrieve, update, and delete products.
- Emit Avro events to Kafka when a product is created, updated, or deleted.
- Cache frequent queries in Redis.
- OpenAPI documentation.
- JWT Security: `ADMIN` and `USER` roles.

---

## 📚 Project Flow
<img src="docs/architecture/Flow.png" alt="Flow">

---

## 🔄 Design Patterns and Principles Applied

- Builder Pattern.
- Singleton Pattern.
- Factory Pattern.
- Strategy Pattern.
- Template Method Pattern.
- Repository Pattern.
- Mapper Pattern.
- Observer Pattern.
- Decorator Pattern.
- Reactive Streams Pattern.

See more in the **[Design Patterns and Principles Page](docs/design-patterns.md)**

---

## 🐘 Kafka Topics

| Event                          | Kafka Topic                 |
|--------------------------------|-----------------------------|
| Product created                | `producto-creado-v1`        |
| Product updated                | `producto-actualizado-v1`   |
| Product deleted                | `producto-eliminado-v1`     |

---
## 🔗 Key REST Endpoints

| Method | URL                                    | Description                          |
|--------|----------------------------------------|--------------------------------------|
| POST   | `/api/v1/auth/login`                   | Login to the system                  |
| POST   | `/api/v1/auth/register`                | Register a new user                  |
| POST   | `/api/v1/products`                     | Create a new product                 |
| GET    | `/api/v1/products`                     | List all products                    |
| GET    | `/api/v1/products/{id}`                | Get product by ID                    |
| GET    | `/api/v1/products/category/{category}` | List products by category            |
| DELETE | `/api/v1/products/{id}`                | Delete a product                     |
| GET    | `/api/v1/products/{id}/price-history`  | Get price history of a product       |

---

## 📆 Installation Requirements

- Docker and Docker Compose installed
- Java 21
- Maven 3.9+

---

## 🛠️ Local Setup

1. Clone the Repository
```bash
git clone https://github.com/santiagoramirez11/mic-product-service.git
cd mic-product-service
```

2. Build the Project
```bash
mvn clean install
```
or use the mvn wrapper:
```bash
.\mvnw clean install
```

3. Start the infrastructure:

```bash
docker-compose up -d
```

4. Run the microservice:

```bash
mvn spring-boot:run
```

The application will be available at http://localhost:8080

💡 Key variables in `application.yml`:
- MongoDB URI: `mongodb://mongo:27017/product`
- Kafka Bootstrap Servers: `kafka:9092`
- Schema Registry URL: `http://schema-registry:8081`
- Redis Host: `redis`

🧪 Testing
- Unit tests with JUnit 5.
- Kafka and MongoDB integration tests using Testcontainers.
- Mocking with Mockito.
- WebFlux testing with WebTestClient.
- Security tests with JWT.

For more details on testing strategies, see the [Testing Documentation](docs/testing/testing.md).

---

## 🔐 Instructions for Login

The application provides an authentication mechanism using a REST endpoint for login. Below are the details and steps to perform login:

### 1. Endpoint Information

- **Endpoint:** `api/v1/auth/login`
- **Method:** `POST`
- **Content-Type:** `application/json`

This endpoint accepts user credentials (username and password) and returns a JWT token upon successful authentication.

### 2. Default Admin Credentials

The application comes with a default admin user configured in the `application.yml` file. These credentials can be modified as per your requirements.

- **Default Username:** `admin`
- **Default Password:** `admin123`

### 3. Example Request

To log in, send a `POST` request to the login endpoint with the following JSON payload:

```json
{
  "username": "admin",
  "password": "admin123"
}
```

### 4. Example Response

Upon successful authentication, the server will return a JSON response containing the JWT token. Below is an example response:

```json
{
  "accessToken": "eyJhbGciOiJIUzI1NiJ9.eyJzdWIiOiJhZG1pbi1hY3R1YXRvciIsInJvbGVzIjoiUk9MRV9BQ1RVQVRPUiIsImlhdCI6MTc0Njk4Mjk4NywiZXhwIjoxNzQ2OTg2NTg3fQ.IUrsEaFngmjrRKenxNR5hp7KVhK6P8LJi90WakEXl-U",
  "tokenType": "Bearer",
  "expiresIn": 3600000
}
```

#### Example Using Postman:
1. Open Postman and create a new `POST` request.
2. Set the URL to `http://localhost:8080/api/v1/auth/login`.
3. Go to the "Body" tab and select "raw".
4. Set the body type to `JSON` and paste the following payload:
   ```json
   {
     "username": "admin",
     "password": "admin123"
   }
   ```
5. Send the request, and you should receive the JWT token in the response.

### 5. Modifying Default Credentials

To modify the default credentials, edit the `application.yml` file and update the values under the appropriate section. For example:

```yaml
app:
  security:
    admin:
      username: admin
      password: admin123
```

After modifying the credentials, restart the application to apply the changes.

### 6. Token Usage

The returned JWT token can be used to authenticate subsequent requests by including it in the `Authorization` header as follows:

```
Authorization: Bearer <your-jwt-token>
```

For example:

```bash
curl -X GET http://localhost:8080/api/v1/products \
-H "Authorization: Bearer eyJhbGciOiJIUzI1NiIsInR5cCI6IkpXVCJ9..."
```

See more about security applied in the **[Security Page](docs/security/security.md)**

---
## 📜 API Documentation
🌍 Swagger UI
Access to interactive documentation:
```bash
https://santiagoramirez11.github.io/mic-product-service/
```