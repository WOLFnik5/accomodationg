A. Tech stack
Java 21
Spring Boot 4
Maven
PostgreSQL
Liquibase
Kafka
Stripe
Telegram
JWT
OpenAPI
Testcontainers

B. Architecture rules
Hexagonal architecture

domain
application
adapter.in
adapter.out
infrastructure

Domain must not depend on Spring/JPA/Kafka/Stripe/Telegram.
Controllers use DTOs only.
Persistence is behind outbound ports.
Kafka producer is adapter.out.kafka.
Kafka consumers are adapter.in.kafka.

C. Domain model summary
Accommodation:
- id: Long
- type: AccommodationType
- location: String
- size: String
- amenities: List<String>
- dailyRate: BigDecimal
- availability: Integer

User:
- id: Long
- email: String
- firstName: String
- lastName: String
- password: String
- role: UserRole

Booking:
- id: Long
- checkInDate: LocalDate
- checkOutDate: LocalDate
- accommodationId: Long
- userId: Long
- status: BookingStatus

Payment:
- id: Long
- status: PaymentStatus
- bookingId: Long
- sessionUrl: String
- sessionId: String
- amountToPay: BigDecimal

D. Enums
AccommodationType: HOUSE, APARTMENT, CONDO, VACATION_HOME
UserRole: ADMIN, CUSTOMER
BookingStatus: PENDING, CONFIRMED, CANCELED, EXPIRED
PaymentStatus: PENDING, PAID, EXPIRED

E. Implemented files/modules
Implemented:
- project skeleton
- domain layer
- application ports
- application services
- persistence adapters
- liquibase
- security
- user api
- accommodation api
- booking api
- stripe adapter
- payment api
- kafka producer
- telegram consumers
- scheduler
- exception handler
- swagger
- docker
- unit tests
- controller tests

Pending:
- integration tests
- final refactor
- README


F. Key decisions
- self-registration creates CUSTOMER only
- booking creation sets status to PENDING
- delete booking endpoint performs logical cancel
- amenities stored with @ElementCollection
- payment amount = days * dailyRate
- payment success handled via GET /payments/success?session_id=...
- expired bookings are checkoutDate <= today and not canceled/expired

G. Package map
com.bookingapp.domain...
com.bookingapp.application...
com.bookingapp.adapter.in.web...
com.bookingapp.adapter.in.kafka...
com.bookingapp.adapter.out.persistence...
com.bookingapp.adapter.out.kafka...
com.bookingapp.adapter.out.stripe...
com.bookingapp.adapter.out.telegram...
com.bookingapp.infrastructure...
