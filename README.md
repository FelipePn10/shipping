---

# 🔒 Shipping System - Backend for Authentication and Logistics Management

![Java](https://img.shields.io/badge/Java-17%2B-007396?logo=java)
![Spring Boot](https://img.shields.io/badge/Spring%20Boot-3.1.0-6DB33F?logo=spring)
![PostgreSQL](https://img.shields.io/badge/PostgreSQL-15%2B-4169E1?logo=postgresql)
![Docker](https://img.shields.io/badge/Docker-20.10%2B-2496ED?logo=docker)
![License](https://img.shields.io/badge/License-MIT-blue)

A robust, scalable backend system built with **Spring Boot** and **Spring Security**, designed for secure user authentication, enterprise management, and logistics operations. The system leverages **JWT** for authentication, **PostgreSQL** for data persistence, and **Docker** for containerized deployments. It supports user management, address handling, wallet transactions, shipment tracking, and more, with a focus on modularity, security, and performance.

## 📑 Table of Contents

- [Features](#-features)
- [Architecture](#-architecture)
- [Prerequisites](#-prerequisites)
- [Installation](#-installation)
- [Environment Configuration](#⚙️-environment-configuration)
- [Project Structure](#-project-structure)
- [API Endpoints](#-api-endpoints)
- [Security Flow](#🛡️-security-flow)
- [Best Practices](#-best-practices)
- [Running Tests](#-running-tests)
- [Deployment](#-deployment)
- [Contributing](#-contributing)
- [License](#-license)
- [Contact](#-contact)

## 🚀 Features

- **Authentication & Authorization**: Secure user and enterprise login with JWT-based authentication.
- **User Management**: Register, update, and retrieve user profiles with role-based access control.
- **Address Management**: Create, update, and manage user addresses with validation.
- **Wallet Operations**: Support for deposits and balance management with error handling.
- **Shipment Tracking**: Manage shipments with status tracking and order item integration.
- **Notifications**: Send email notifications for password resets and other events.
- **Enterprise Support**: Dedicated endpoints for enterprise registration and authentication.
- **Error Handling**: Comprehensive exception handling with meaningful error responses.
- **Containerization**: Dockerized PostgreSQL and application setup for consistent deployments.
- **Logging**: Detailed logging with SLF4J for debugging and monitoring.
- **Scalability**: Modular architecture with service, repository, and DTO layers.

## 🏛 Architecture

The system follows a **layered architecture** with clear separation of concerns:

- **Controllers**: Handle HTTP requests and responses, delegating to services.
- **Services**: Contain business logic, interacting with repositories and external services.
- **Repositories**: Manage data access using Spring Data JPA.
- **Entities**: Represent database tables with JPA annotations.
- **DTOs**: Facilitate data transfer between layers, ensuring encapsulation.
- **Security**: Implements JWT-based authentication with Spring Security.
- **Mappers**: Convert between entities and DTOs using MapStruct.
- **Configuration**: Centralized configuration for caching, security, and external integrations (e.g., Stripe, WebClient).

The project uses **Docker Compose** for orchestrating the PostgreSQL database and application container.

## 📋 Prerequisites

- **Java JDK 17+**
- **Apache Maven 3.8+**
- **Docker 20.10+**
- **Docker Compose 2.12+**
- **PostgreSQL 15+**
- **IDE**: IntelliJ IDEA, VS Code, or equivalent
- **API Testing Tool**: Postman or curl
- **Optional**: pgAdmin or DBeaver for database management

## 🛠 Installation

1. **Clone the Repository**:
   ```bash
   git clone https://github.com/your-username/shipping-system.git
   cd shipping-system
   ```

2. **Set Up Environment Variables**:
   Copy the example configuration file and update it with your settings:
   ```bash
   cp shipping/src/main/resources/application.properties.example shipping/src/main/resources/application.properties
   ```

3. **Start PostgreSQL with Docker**:
   ```bash
   cd infrastructure/docker
   docker-compose up -d
   ```

4. **Build and Run the Application**:
   ```bash
   cd ../../shipping
   mvn clean install
   mvn spring-boot:run
   ```

The application will be available at `http://localhost:8080`.

## ⚙️ Environment Configuration

Configure the application in `shipping/src/main/resources/application.properties`:

```properties
# Database
spring.datasource.url=jdbc:postgresql://localhost:5432/shipping_db
spring.datasource.username=authuser
spring.datasource.password=securepassword
spring.jpa.hibernate.ddl-auto=update

# JWT
jwt.secret=SuperSegredoJWT12345!MuitoLongaESegura
jwt.expiration=86400000

# Email Service
spring.mail.host=smtp.gmail.com
spring.mail.port=587
spring.mail.username=your-email@gmail.com
spring.mail.password=your-app-password

# Stripe (Payment Integration)
stripe.api.key=your-stripe-api-key

# Logging
logging.level.org.springframework.security=DEBUG
logging.level.redirex.shipping=DEBUG
```

| Variable                    | Description                              | Default Value                          |
|-----------------------------|------------------------------------------|----------------------------------------|
| `spring.datasource.url`     | PostgreSQL connection URL                | `jdbc:postgresql://localhost:5432/shipping_db` |
| `spring.datasource.username`| Database username                         | `authuser`                             |
| `spring.datasource.password`| Database password                         | `securepassword`                       |
| `jwt.secret`                | JWT signing key                          | (Required, must be secure)             |
| `jwt.expiration`            | JWT expiration time (ms)                 | `86400000` (24 hours)                  |
| `spring.mail.*`             | Email server configuration               | (SMTP settings, e.g., Gmail)           |
| `stripe.api.key`            | Stripe API key for payments              | (Required for payment features)         |

## 🏗 Project Structure

```plaintext
├── infrastructure/
│   └── docker/
│       ├── docker-compose.yml       # PostgreSQL and app container setup
│       ├── docker-compose.override.yml
│       └── Dockerfile              # Application container definition
├── shipping/
│   ├── mvnw                        # Maven wrapper
│   ├── pom.xml                     # Maven dependencies
│   ├── src/
│   │   ├── main/
│   │   │   ├── java/redirex/shipping/
│   │   │   │   ├── config/         # Application configurations (e.g., Security, Cache)
│   │   │   │   ├── controller/     # REST controllers (Auth, User, Enterprise, Email)
│   │   │   │   ├── dto/            # Data Transfer Objects for API communication
│   │   │   │   ├── entity/         # JPA entities for database mapping
│   │   │   │   ├── enums/          # Enum definitions (e.g., CouponType, ShipmentStatus)
│   │   │   │   ├── exception/      # Custom exception handling
│   │   │   │   ├── mapper/         # Entity-DTO mapping with MapStruct
│   │   │   │   ├── repositories/   # JPA repositories for data access
│   │   │   │   ├── security/       # JWT and Spring Security configurations
│   │   │   │   ├── service/        # Business logic services
│   │   │   │   └── util/           # Utility classes (e.g., CouponCodeGenerator)
│   │   │   └── resources/
│   │   │       ├── application.properties  # Application settings
│   │   │       └── META-INF/
│   │   └── test/                   # Unit and integration tests
│   └── target/                     # Compiled classes and generated sources
└── LICENSE                         # MIT License file
```

## 🌐 API Endpoints

### Authentication (`/public/auth`)
| Method | Endpoint                | Description                       | Request Body                     | Response                     |
|--------|-------------------------|-----------------------------------|----------------------------------|------------------------------|
| `POST` | `/public/auth/login`    | Authenticate a user               | `AuthRequestDTO`                 | `AuthResponseDTO` (JWT token) |
| `POST` | `/public/auth/login/enterprise` | Authenticate an enterprise | `AuthRequestDTO`                 | `AuthResponseDTO` (JWT token) |
| `POST` | `/public/auth/logout`   | Invalidate JWT token              | None (Authorization header)      | Success message              |

### User Management (`/public/user` and `/api/user`)
| Method | Endpoint                       | Description                          | Request Body                     | Response                     |
|--------|--------------------------------|--------------------------------------|----------------------------------|------------------------------|
| `POST` | `/public/user/register`        | Register a new user                  | `RegisterUserDTO`                | `UserResponse`               |
| `POST` | `/public/user/forgot-password` | Request password reset               | `ForgotPasswordDTO`              | Success message              |
| `POST` | `/public/user/reset-password`  | Reset user password                  | `ResetPasswordDTO`               | Success message              |
| `POST` | `/public/user/created-address` | Create a new address                 | `CreateAddressRequest`           | `AddressResponse`            |
| `PUT`  | `/public/user/update-address/{zipcode}` | Update an address           | `AddressDTO`                     | `AddressResponse`            |
| `GET`  | `/api/user/{id}`              | Retrieve user by ID (authenticated)  | None                             | `UserResponse`               |
| `PUT`  | `/api/user/{id}/profile`      | Update user profile (authenticated)  | `RegisterUserDTO`                | `UserResponse`               |

### Email Notifications (`/email`)
| Method | Endpoint         | Description                     | Request Body                     | Response                     |
|--------|------------------|---------------------------------|----------------------------------|------------------------------|
| `POST` | `/email/send`    | Send an email notification       | `UserEmailDetailsUtil`           | Success or error message     |

### Example Request (User Registration)
```http
POST /public/user/register
Content-Type: application/json

{
  "fullname": "John Doe",
  "email": "john.doe@example.com",
  "password": "SecurePass123!",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "123 Main St",
  "complement": "Apt 4B",
  "city": "São Paulo",
  "state": "SP",
  "zipcode": "12345678",
  "country": "Brazil",
  "occupation": "Developer"
}

# Response
HTTP/1.1 201 Created
{
  "id": 1,
  "fullname": "John Doe",
  "email": "john.doe@example.com",
  "cpf": "12345678901",
  "phone": "11987654321",
  "address": "123 Main St",
  "complement": "Apt 4B",
  "city": "São Paulo",
  "state": "SP",
  "zipcode": "12345678",
  "country": "Brazil",
  "occupation": "Developer",
  "role": "ROLE_USER"
}
```

## 🛡️ Security Flow

1. **Registration**: Users register with validated inputs, storing encrypted passwords (BCrypt).
2. **Authentication**: Users or enterprises log in, receiving a JWT token upon successful credential validation.
3. **Authorization**: Protected endpoints require a valid JWT in the `Authorization: Bearer <token>` header. The `JwtAuthenticationFilter` validates tokens, and Spring Security enforces role-based access.
4. **Logout**: Tokens are blacklisted using the `TokenBlacklistService` to prevent reuse.
5. **Password Reset**: Users request a reset token via email, which must be validated within a time window to update the password.

## 🔐 Best Practices

- **Security**:
  - Passwords encrypted with `BCryptPasswordEncoder`.
  - JWT tokens with secure signing and configurable expiration.
  - Stateless session management (`SessionCreationPolicy.STATELESS`).
  - Role-based access control with `@PreAuthorize`.
- **Code Quality**:
  - Modular design with clear separation of concerns.
  - Use of DTOs and MapStruct for data mapping.
  - Comprehensive exception handling with custom exceptions.
- **Performance**:
  - Caching configured via `CacheConfig`.
  - Efficient database queries with Spring Data JPA.
- **Logging**:
  - Detailed SLF4J logs for debugging and monitoring.
  - Configurable log levels in `application.properties`.
- **Validation**:
  - Input validation using Jakarta Bean Validation.
  - Custom error responses with timestamps and status codes.

## 🧪 Running Tests

The project includes basic test setup in `GlobalApplicationTests.java`. To run tests:

```bash
mvn test
```

For manual API testing, use Postman or curl:

```bash
# Test user registration
curl -X POST http://localhost:8080/public/user/register \
  -H "Content-Type: application/json" \
  -d '{"fullname":"John Doe","email":"john.doe@example.com","password":"SecurePass123!","cpf":"12345678901","phone":"11987654321","address":"123 Main St","complement":"Apt 4B","city":"São Paulo","state":"SP","zipcode":"12345678","country":"Brazil","occupation":"Developer"}'

# Test user login
curl -X POST http://localhost:8080/public/auth/login \
  -H "Content-Type: application/json" \
  -d '{"email":"john.doe@example.com","password":"SecurePass123!"}'
```

## 🚀 Deployment

1. **Build the Docker Image**:
   ```bash
   cd shipping
   docker build -t shipping-system:latest .
   ```

2. **Run with Docker Compose**:
   ```bash
   cd infrastructure/docker
   docker-compose up -d
   ```

3. **Verify Deployment**:
   Ensure the application is running at `http://localhost:8080` and the database is accessible.

## 🤝 Contributing

We welcome contributions! Follow these steps:

1. Fork the repository.
2. Create a feature branch (`git checkout -b feature/your-feature`).
3. Commit changes (`git commit -m 'Add your feature'`).
4. Push to the branch (`git push origin feature/your-feature`).
5. Open a Pull Request with a detailed description.

Please adhere to the [Code of Conduct](CODE_OF_CONDUCT.md) and ensure tests pass before submitting.

## 📄 License

Distributed under the MIT License. See `LICENSE` for details.

## ✉️ Contact

**Maintainer**: Felipe Panosso  
**Email**: panossodev@gmail.com  
**LinkedIn**: [linkedin.com/in/felipe-panosso](https://linkedin.com/in/felipe-panosso)  

---
