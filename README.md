# Booking App

## Project Overview

Booking App is a production-style backend service for managing accommodation listings, customer bookings, payments, and operational notifications.  
It is designed as a portfolio project that demonstrates clean architecture, domain-driven modeling, secure REST APIs, asynchronous event publishing, external payment integration, and containerized local development.

The application exposes a JWT-secured API for customers and administrators, persists data in PostgreSQL, publishes business events to Kafka, integrates with Stripe for checkout sessions, and sends Telegram notifications for important booking and payment events.

## Features

- Customer registration and login with JWT authentication
- Public accommodation catalog browsing
- Admin-only accommodation management
- Customer booking creation, update, cancellation, and self-service listing
- Admin booking filtering and oversight
- Stripe checkout session creation and payment completion handling
- Kafka-based event publishing for accommodation, booking, and payment events
- Telegram notifications triggered from Kafka consumers
- Scheduled expired booking processing
- Liquibase-managed schema versioning
- MockMvc controller tests, application service unit tests, and Testcontainers integration test setup

## Tech Stack

- Java 21
- Spring Boot 4
- Spring Web MVC
- Spring Security
- Spring Data JPA
- PostgreSQL
- Liquibase
- Apache Kafka
- Stripe Java SDK
- Telegram Bot API
- springdoc OpenAPI / Swagger UI
- Maven
- Docker and Docker Compose
- JUnit 5
- Mockito
- Testcontainers

## Architecture

### Hexagonal Architecture

The project follows a hexagonal architecture to keep the business core independent from frameworks and infrastructure.

Package layout:

- `com.bookingapp.domain`
  Pure domain models, enums, and domain exceptions.
- `com.bookingapp.application`
  Use cases, commands, queries, ports, events, and application services.
- `com.bookingapp.adapter.in`
  Incoming adapters such as REST controllers and Kafka consumers.
- `com.bookingapp.adapter.out`
  Outgoing adapters for persistence, Kafka, Stripe, and Telegram.
- `com.bookingapp.infrastructure`
  Framework-specific configuration, security, and scheduling.

Key design principles:

- The domain layer does not depend on Spring, JPA, Kafka, Stripe, or Telegram.
- Controllers work with request/response DTOs and delegate to use cases.
- Persistence is accessed through outbound ports and implemented in adapters.
- External integrations are isolated behind explicit application ports.

### Kafka Usage

Kafka is used for asynchronous business event delivery.  
The application publishes events such as:

- accommodation created
- booking created
- booking canceled
- booking expired
- payment succeeded

Publishing flow:

1. An application service completes a business action.
2. The service invokes `EventPublisherPort`.
3. `KafkaEventPublisherAdapter` sends the event to the configured Kafka topic.

Consumption flow:

1. `TelegramEventConsumer` listens to Kafka topics.
2. Payloads are deserialized into application event records.
3. Human-readable Telegram messages are generated and sent through the notification use case.

### Stripe Integration

Stripe is used to create checkout sessions for bookings.

The payment flow is:

1. A customer creates a booking.
2. The client requests a payment session from `POST /payments`.
3. The Stripe adapter creates a hosted checkout session.
4. Stripe redirects the user back to the success or cancel endpoint.
5. The application updates payment state and publishes a payment event when appropriate.

### Telegram Notifications

Telegram notifications are implemented through a dedicated outbound adapter and Kafka-driven consumers.

Notifications are designed to keep operational communication decoupled from core business actions:

- application services publish events
- Kafka transports those events
- Telegram consumers format messages
- Telegram outbound adapter delivers them through the Bot API

## Domain Overview

The project models four main business concepts.

### User

Represents a system account.

- `id`
- `email`
- `firstName`
- `lastName`
- `password`
- `role`

Roles:

- `ADMIN`
- `CUSTOMER`

### Accommodation

Represents a bookable property listing.

- `id`
- `type`
- `location`
- `size`
- `amenities`
- `dailyRate`
- `availability`

Types:

- `HOUSE`
- `APARTMENT`
- `CONDO`
- `VACATION_HOME`

### Booking

Represents a reservation for an accommodation and date range.

- `id`
- `checkInDate`
- `checkOutDate`
- `accommodationId`
- `userId`
- `status`

Statuses:

- `PENDING`
- `CONFIRMED`
- `CANCELED`
- `EXPIRED`

### Payment

Represents a payment attempt and its checkout metadata.

- `id`
- `status`
- `bookingId`
- `sessionUrl`
- `sessionId`
- `amountToPay`

Statuses:

- `PENDING`
- `PAID`
- `EXPIRED`

## REST API Overview

### Authentication

- `POST /auth/register`  
  Register a new customer account.
- `POST /auth/login`  
  Authenticate and receive a JWT bearer token.

### Users

- `GET /users/me`  
  Get the currently authenticated user profile.
- `PUT /users/me`  
  Replace the current profile.
- `PATCH /users/me`  
  Partially update the current profile.
- `PUT /users/{id}/role`  
  Update a user role. Admin only.

### Accommodations

- `GET /accommodations`  
  Public accommodation list.
- `GET /accommodations/{id}`  
  Public accommodation details.
- `POST /accommodations`  
  Create accommodation. Admin only.
- `PUT /accommodations/{id}`  
  Replace accommodation. Admin only.
- `PATCH /accommodations/{id}`  
  Partially update accommodation. Admin only.
- `DELETE /accommodations/{id}`  
  Delete accommodation. Admin only.

### Bookings

- `POST /bookings`  
  Create a booking.
- `GET /bookings`  
  List bookings with admin filters. Admin only.
- `GET /bookings/my`  
  List the current user's bookings.
- `GET /bookings/{id}`  
  Get booking details.
- `PUT /bookings/{id}`  
  Replace booking dates.
- `PATCH /bookings/{id}`  
  Partially update booking dates.
- `DELETE /bookings/{id}`  
  Logically cancel a booking.

### Payments

- `GET /payments`  
  List payments. Admins can filter by user; customers only see their own.
- `POST /payments`  
  Create a Stripe checkout session for a booking.
- `GET /payments/success?session_id=...`  
  Handle successful checkout redirect.
- `GET /payments/cancel?session_id=...`  
  Handle canceled checkout redirect.

## Security Overview

The API uses Spring Security with stateless JWT authentication.

Authentication model:

- registration and login are public
- JWT bearer tokens are required for protected endpoints
- user identity is extracted through a custom JWT filter

Authorization rules:

- public:
  - `/auth/**`
  - `GET /accommodations`
  - `GET /accommodations/{id}`
  - payment redirect endpoints
  - Swagger/OpenAPI and health check endpoints
- authenticated:
  - `/users/me`
  - `/bookings/**`
  - payment management endpoints
- admin only:
  - accommodation write operations
  - `PUT /users/{id}/role`
  - `GET /bookings` with filters

Security components:

- `JwtTokenService`
- `JwtAuthenticationFilter`
- `RestAuthenticationEntryPoint`
- `RestAccessDeniedHandler`
- BCrypt password encoding

## How to Run Locally

### Prerequisites

- Java 21
- Maven 3.9+
- PostgreSQL
- Kafka

### 1. Configure Environment

Create a local `.env` file using `.env.sample` as the reference.

### 2. Start Required Services

You can either run PostgreSQL and Kafka manually or use Docker Compose.

### 3. Run the Application

```bash
mvn spring-boot:run
```

By default, the application starts on:

```text
http://localhost:8080
```

## Docker Setup

The project includes a full Docker-based local environment:

- PostgreSQL
- Zookeeper
- Kafka
- Kafka UI
- Booking App

### Start Everything

```bash
docker compose up --build
```

### Exposed Services

- Application: `http://localhost:8080`
- Swagger UI: `http://localhost:8080/swagger-ui.html`
- Kafka UI: `http://localhost:8081`
- PostgreSQL: `localhost:5432`
- Kafka: `localhost:9092`

## Environment Variables

Main environment variables used by the project:

### General

- `SPRING_PROFILES_ACTIVE`
- `SERVER_PORT`

### Database

- `DB_URL`
- `DB_USERNAME`
- `DB_PASSWORD`
- `POSTGRES_DB`

### JWT

- `JWT_SECRET`
- `JWT_EXPIRATION_MINUTES`

### Kafka

- `KAFKA_BOOTSTRAP_SERVERS`
- `KAFKA_CONSUMER_GROUP_ID`
- `KAFKA_TOPIC_BOOKING_CREATED`
- `KAFKA_TOPIC_BOOKING_CANCELED`
- `KAFKA_TOPIC_BOOKING_EXPIRED`
- `KAFKA_TOPIC_ACCOMMODATION_CREATED`
- `KAFKA_TOPIC_PAYMENT_SUCCEEDED`

### Stripe

- `STRIPE_SECRET_KEY`
- `STRIPE_SUCCESS_URL`
- `STRIPE_CANCEL_URL`
- `STRIPE_CURRENCY`

### Telegram

- `TELEGRAM_BOT_TOKEN`
- `TELEGRAM_CHAT_ID`

### Scheduler

- `BOOKING_EXPIRATION_CRON`

See `.env.sample` for example values.

## Swagger Link

Swagger UI is available at:

```text
http://localhost:8080/swagger-ui.html
```

OpenAPI JSON is available at:

```text
http://localhost:8080/api-docs
```

## Testing

The project includes several layers of automated testing:

- application service unit tests
- WebMvc controller tests with MockMvc
- Testcontainers-based integration test setup for PostgreSQL

Run the full test suite with:

```bash
mvn test
```

Notes:

- controller and service tests run without Docker
- Testcontainers-based integration tests are enabled and will run when Docker is available
- Liquibase schema validation is configured through `ddl-auto=validate`

## Future Improvements

- Add full end-to-end integration tests with live PostgreSQL and Kafka in CI
- Add API versioning strategy
- Introduce idempotency safeguards for payment callbacks
- Improve payment and booking lifecycle reporting
- Extend role-based access control for finer-grained administration
- Add observability dashboards and structured tracing
- Add frontend or client application for a complete booking flow demo
- Improve OpenAPI examples and request/response documentation

## Portfolio Notes

This project is intentionally structured to showcase:

- clean architecture and separation of concerns
- secure backend API design
- event-driven integration patterns
- external service integration
- maintainable testing strategy
- realistic containerized developer experience

