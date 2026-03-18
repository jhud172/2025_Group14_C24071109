package uk.ac.cf._5.group14.One_To_One.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaCompatibilityInitializer {

    static final String HAS_USERS_COLUMN_SQL = """
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE UPPER(table_name) = 'USERS'
        AND UPPER(column_name) = UPPER(?)
        """;

    static final List<UserColumnPatch> USER_COLUMN_PATCHES = List.of(
        new UserColumnPatch("date_of_birth", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS date_of_birth DATE
            """),
        new UserColumnPatch("phone_number", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS phone_number VARCHAR(30)
            """),
        new UserColumnPatch("phone_country", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS phone_country VARCHAR(2) NOT NULL DEFAULT 'GB'
            """),
        new UserColumnPatch("phone_verified", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS phone_verified BOOLEAN NOT NULL DEFAULT FALSE
            """),
        new UserColumnPatch("phone_verified_at", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS phone_verified_at TIMESTAMP
            """),
        new UserColumnPatch("email_verified", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS email_verified BOOLEAN NOT NULL DEFAULT FALSE
            """),
        new UserColumnPatch("email_verified_at", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS email_verified_at TIMESTAMP
            """),
        new UserColumnPatch("username_changed_at", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS username_changed_at TIMESTAMP
            """),
        new UserColumnPatch("bio", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS bio VARCHAR(800)
            """),
        new UserColumnPatch("profile_image_url", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS profile_image_url VARCHAR(300)
            """),
        new UserColumnPatch("role", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS role VARCHAR(30) NOT NULL DEFAULT 'CLIENT'
            """),
        new UserColumnPatch("gym_id", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS gym_id BIGINT
            """),
        new UserColumnPatch("trainer_profile_id", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS trainer_profile_id BIGINT
            """),
        new UserColumnPatch("trainer_verified", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS trainer_verified BOOLEAN NOT NULL DEFAULT FALSE
            """),
        new UserColumnPatch("has_seen_tutorial", """
            ALTER TABLE users
            ADD COLUMN IF NOT EXISTS has_seen_tutorial BOOLEAN NOT NULL DEFAULT FALSE
            """)
    );

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void ensureUsersCompatibilityColumns() {
        for (UserColumnPatch patch : USER_COLUMN_PATCHES) {
            if (hasUsersColumn(patch.columnName())) {
                log.debug("Schema compatibility check: users.{} already exists.", patch.columnName());
                continue;
            }

            jdbcTemplate.execute(patch.ddl());
            log.warn("Applied schema compatibility patch: added missing users.{} column.", patch.columnName());
        }
    }

    private boolean hasUsersColumn(String columnName) {
        Integer existingColumns = jdbcTemplate.queryForObject(
            HAS_USERS_COLUMN_SQL,
            Integer.class,
            columnName
        );
        return existingColumns != null && existingColumns > 0;
    }

    record UserColumnPatch(String columnName, String ddl) {}
}
