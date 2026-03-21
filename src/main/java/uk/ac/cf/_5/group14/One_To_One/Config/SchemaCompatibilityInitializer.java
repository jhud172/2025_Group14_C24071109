package uk.ac.cf._5.group14.One_To_One.Config;

import jakarta.annotation.PostConstruct;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Slf4j
@Component
@RequiredArgsConstructor
public class SchemaCompatibilityInitializer {
    static final String USERS_TABLE = "users";
    static final String GYM_PROFILES_TABLE = "gym_profiles";
    static final String GYM_MEMBERSHIP_PRODUCTS_TABLE = "gym_membership_products";
    static final String GYM_MEMBER_SUBSCRIPTIONS_TABLE = "gym_member_subscriptions";
    static final String PRICE_CHANGE_EVENTS_TABLE = "price_change_events";
    static final String TRAINER_VERIFICATION_REQUESTS_TABLE = "trainer_verification_requests";

    static final String HAS_USERS_COLUMN_SQL = """
        SELECT COUNT(*)
        FROM information_schema.columns
        WHERE UPPER(table_name) = 'USERS'
        AND UPPER(column_name) = UPPER(?)
        """;

    static final String HAS_TABLE_SQL = """
        SELECT COUNT(*)
        FROM information_schema.tables
        WHERE UPPER(table_name) = UPPER(?)
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

    static final String DEV_MODE_PAGE_SETTINGS_TABLE = "dev_mode_page_settings";

    static final String CREATE_DEV_MODE_PAGE_SETTINGS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS dev_mode_page_settings
        (
            id BIGSERIAL PRIMARY KEY,
            page_key VARCHAR(80) NOT NULL UNIQUE,
            access_mode VARCHAR(20) NOT NULL DEFAULT 'ENABLED',
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP
        )
        """;

    static final String CREATE_GYM_MEMBERSHIP_PRODUCTS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS gym_membership_products
        (
            id BIGSERIAL PRIMARY KEY,
            gym_id BIGINT NOT NULL,
            name VARCHAR(200) NOT NULL,
            description TEXT NULL,
            price_cents INTEGER NOT NULL,
            billing_period VARCHAR(20) NOT NULL DEFAULT 'MONTHLY',
            active BOOLEAN NOT NULL DEFAULT TRUE,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_gym_membership_products_gym
                FOREIGN KEY (gym_id) REFERENCES gym_profiles (id)
                    ON DELETE CASCADE
        )
        """;

    static final String CREATE_GYM_MEMBER_SUBSCRIPTIONS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS gym_member_subscriptions
        (
            id BIGSERIAL PRIMARY KEY,
            user_id BIGINT NOT NULL,
            gym_id BIGINT NOT NULL,
            product_id BIGINT NOT NULL,
            status VARCHAR(20) NOT NULL DEFAULT 'ACTIVE',
            started_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            renews_at TIMESTAMP NOT NULL,
            cancelled_at TIMESTAMP NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT uq_gym_member_subscriptions_user_gym UNIQUE (user_id, gym_id),
            CONSTRAINT fk_gym_member_subscriptions_user
                FOREIGN KEY (user_id) REFERENCES users (id)
                    ON DELETE CASCADE,
            CONSTRAINT fk_gym_member_subscriptions_gym
                FOREIGN KEY (gym_id) REFERENCES gym_profiles (id)
                    ON DELETE CASCADE,
            CONSTRAINT fk_gym_member_subscriptions_product
                FOREIGN KEY (product_id) REFERENCES gym_membership_products (id)
                    ON DELETE CASCADE
        )
        """;

    static final String CREATE_PRICE_CHANGE_EVENTS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS price_change_events
        (
            id BIGSERIAL PRIMARY KEY,
            gym_id BIGINT NOT NULL,
            product_id BIGINT NOT NULL,
            old_price_cents INTEGER NOT NULL,
            new_price_cents INTEGER NOT NULL,
            effective_at TIMESTAMP NOT NULL,
            reason VARCHAR(500) NOT NULL,
            changed_by_user_id BIGINT NOT NULL,
            affected_member_count INTEGER NOT NULL DEFAULT 0,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_price_change_events_gym
                FOREIGN KEY (gym_id) REFERENCES gym_profiles (id)
                    ON DELETE CASCADE,
            CONSTRAINT fk_price_change_events_product
                FOREIGN KEY (product_id) REFERENCES gym_membership_products (id)
                    ON DELETE CASCADE,
            CONSTRAINT fk_price_change_events_changed_by
                FOREIGN KEY (changed_by_user_id) REFERENCES users (id)
                    ON DELETE RESTRICT
        )
        """;

    static final String CREATE_TRAINER_VERIFICATION_REQUESTS_TABLE_SQL = """
        CREATE TABLE IF NOT EXISTS trainer_verification_requests
        (
            id BIGSERIAL PRIMARY KEY,
            trainer_user_id BIGINT NOT NULL,
            gym_id BIGINT NULL,
            status VARCHAR(20) NOT NULL DEFAULT 'PENDING',
            notes TEXT NULL,
            admin_notes TEXT NULL,
            submitted_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            reviewed_at TIMESTAMP NULL,
            reviewed_by_user_id BIGINT NULL,
            created_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            updated_at TIMESTAMP NOT NULL DEFAULT CURRENT_TIMESTAMP,
            CONSTRAINT fk_trainer_verification_requests_trainer
                FOREIGN KEY (trainer_user_id) REFERENCES users (id)
                    ON DELETE CASCADE,
            CONSTRAINT fk_trainer_verification_requests_gym
                FOREIGN KEY (gym_id) REFERENCES gym_profiles (id)
                    ON DELETE SET NULL,
            CONSTRAINT fk_trainer_verification_requests_reviewer
                FOREIGN KEY (reviewed_by_user_id) REFERENCES users (id)
                    ON DELETE SET NULL
        )
        """;

    private final JdbcTemplate jdbcTemplate;

    @PostConstruct
    void ensureUsersCompatibilityColumns() {
        if (!hasTable(USERS_TABLE)) {
            log.warn("Skipping users column compatibility patches because '{}' does not exist yet.", USERS_TABLE);
            return;
        }

        for (UserColumnPatch patch : USER_COLUMN_PATCHES) {
            if (hasUsersColumn(patch.columnName())) {
                log.debug("Schema compatibility check: users.{} already exists.", patch.columnName());
                continue;
            }

            jdbcTemplate.execute(patch.ddl());
            log.warn("Applied schema compatibility patch: added missing users.{} column.", patch.columnName());
        }
    }

    @PostConstruct
    void ensureDevModePageSettingsTable() {
        ensureTableIfMissing(DEV_MODE_PAGE_SETTINGS_TABLE, CREATE_DEV_MODE_PAGE_SETTINGS_TABLE_SQL);
    }

    @PostConstruct
    void ensureAdditionalCompatibilityTables() {
        ensureTableIfMissing(
            GYM_MEMBERSHIP_PRODUCTS_TABLE,
            CREATE_GYM_MEMBERSHIP_PRODUCTS_TABLE_SQL,
            GYM_PROFILES_TABLE
        );
        ensureTableIfMissing(
            GYM_MEMBER_SUBSCRIPTIONS_TABLE,
            CREATE_GYM_MEMBER_SUBSCRIPTIONS_TABLE_SQL,
            USERS_TABLE,
            GYM_PROFILES_TABLE,
            GYM_MEMBERSHIP_PRODUCTS_TABLE
        );
        ensureTableIfMissing(
            PRICE_CHANGE_EVENTS_TABLE,
            CREATE_PRICE_CHANGE_EVENTS_TABLE_SQL,
            USERS_TABLE,
            GYM_PROFILES_TABLE,
            GYM_MEMBERSHIP_PRODUCTS_TABLE
        );
        ensureTableIfMissing(
            TRAINER_VERIFICATION_REQUESTS_TABLE,
            CREATE_TRAINER_VERIFICATION_REQUESTS_TABLE_SQL,
            USERS_TABLE,
            GYM_PROFILES_TABLE
        );
    }

    private boolean hasUsersColumn(String columnName) {
        Integer existingColumns = jdbcTemplate.queryForObject(
            HAS_USERS_COLUMN_SQL,
            Integer.class,
            columnName
        );
        return existingColumns != null && existingColumns > 0;
    }

    private boolean hasTable(String tableName) {
        Integer existingTables = jdbcTemplate.queryForObject(
            HAS_TABLE_SQL,
            Integer.class,
            tableName
        );
        return existingTables != null && existingTables > 0;
    }

    private void ensureTableIfMissing(String tableName, String ddl, String... requiredTables) {
        if (hasTable(tableName)) {
            log.debug("Schema compatibility check: {} already exists.", tableName);
            return;
        }

        List<String> missingDependencies = Arrays.stream(requiredTables)
            .filter(requiredTable -> !hasTable(requiredTable))
            .toList();
        if (!missingDependencies.isEmpty()) {
            log.warn("Skipping schema compatibility patch for {} because required tables are missing: {}.",
                tableName, String.join(", ", missingDependencies));
            return;
        }

        jdbcTemplate.execute(ddl);
        log.warn("Applied schema compatibility patch: created missing {} table.", tableName);
    }

    record UserColumnPatch(String columnName, String ddl) {}
}
