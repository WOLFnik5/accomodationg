package com.bookingapp;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;
import org.testcontainers.junit.jupiter.Container;
import org.testcontainers.junit.jupiter.Testcontainers;

@Testcontainers(disabledWithoutDocker = true)
@SpringBootTest
class BookingAppApplicationTests {

    @Container
    static final PostgreSQLContainer<?> POSTGRESQL_CONTAINER = new PostgreSQLContainer<>("postgres:16-alpine")
            .withDatabaseName("booking_app_test")
            .withUsername("booking_test")
            .withPassword("booking_test");

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", POSTGRESQL_CONTAINER::getJdbcUrl);
        registry.add("spring.datasource.username", POSTGRESQL_CONTAINER::getUsername);
        registry.add("spring.datasource.password", POSTGRESQL_CONTAINER::getPassword);
        registry.add("spring.datasource.driver-class-name", POSTGRESQL_CONTAINER::getDriverClassName);
        registry.add("spring.kafka.bootstrap-servers", () -> "localhost:9092");
        registry.add("app.security.jwt.secret", () -> "c3VwZXItc2VjdXJlLWJhc2U2NC1zZWNyZXQtdGhhdC1pcy1sb25nLWVub3VnaA==");
        registry.add("app.security.jwt.expiration-minutes", () -> "60");
    }

    @Test
    void contextLoads() {
    }
}
