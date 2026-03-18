package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaCompatibilityInitializer {

    static final String HAS_USERS_DATE_OF_BIRTH_COLUMN_SQL = """
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE UPPER(table_name) = 'USERS'
        AND UPPER(column_name) = 'DATE_OF_BIRTH'
        """;

    static final String ADD_USERS_DATE_OF_BIRTH_COLUMN_SQL = """
        ALTER TABLE users
        ADD COLUMN IF NOT EXISTS date_of_birth DATE
    """;

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void ensureUsersDateOfBirthColumn() {
        Integer existingColumns = jdbcTemplate.queryForObject(
            HAS_USERS_DATE_OF_BIRTH_COLUMN_SQL,
            Integer.class
        );

        if (existingColumns != null && existingColumns > 0) {
            log.debug("Schema compatibility check: users.date_of_birth already exists.");
            return;
        }

        jdbcTemplate.execute(ADD_USERS_DATE_OF_BIRTH_COLUMN_SQL);
        log.warn("Applied schema compatibility patch: added missing users.date_of_birth column.");
    }
}
