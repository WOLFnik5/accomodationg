package com.bookingapp;

import com.bookingapp.support.PostgreSqlLiquibaseIntegrationTestSupport;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;

import static org.assertj.core.api.Assertions.assertThat;

@SpringBootTest
class LiquibaseMigrationIntegrationTest extends PostgreSqlLiquibaseIntegrationTestSupport {

    @Autowired
    private JdbcTemplate jdbcTemplate;

    @Test
    void shouldCreateDatabaseChangelogTable() {
        Integer databaseChangelogTableCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.tables
                where table_schema = 'public'
                  and table_name = 'databasechangelog'
                """,
                Integer.class
        );

        assertThat(databaseChangelogTableCount).isEqualTo(1);
    }

    @Test
    void shouldCreateUsersAndPaymentsTables() {
        Integer usersTableCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.tables
                where table_schema = 'public'
                  and table_name = 'users'
                """,
                Integer.class
        );
        Integer paymentsTableCount = jdbcTemplate.queryForObject(
                """
                select count(*)
                from information_schema.tables
                where table_schema = 'public'
                  and table_name = 'payments'
                """,
                Integer.class
        );

        assertThat(usersTableCount).isEqualTo(1);
        assertThat(paymentsTableCount).isEqualTo(1);
    }
}
