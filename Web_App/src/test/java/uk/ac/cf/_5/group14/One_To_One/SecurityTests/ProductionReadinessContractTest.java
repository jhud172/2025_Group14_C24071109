package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.yaml.snakeyaml.Yaml;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Map;

import static org.assertj.core.api.Assertions.assertThat;

class ProductionReadinessContractTest {

    private static final Path RESOURCES = Path.of("src/main/resources");

    @Test
    void renderUsesVersionedMigrationsWithoutAutomaticDemoData() throws IOException {
        String renderProperties = Files.readString(RESOURCES.resolve("application-render.properties"));
        Path baselineMigration = RESOURCES.resolve("db/migration/postgresql/V1__baseline_schema.sql");
        Path chatThreadMigration =
                RESOURCES.resolve("db/migration/postgresql/V2__add_chat_thread_type_and_peer.sql");
        Path healthRecordMigration =
                RESOURCES.resolve("db/migration/postgresql/V3__align_health_record_numeric_types.sql");
        Path savedPaymentMigration =
                RESOURCES.resolve("db/migration/postgresql/V4__align_saved_payment_last_four_type.sql");

        assertThat(renderProperties)
                .contains("spring.sql.init.mode=never")
                .contains("spring.flyway.enabled=true")
                .contains("spring.flyway.locations=classpath:db/migration/postgresql")
                .contains("spring.jpa.properties.hibernate.default_schema=${APP_DATABASE_SCHEMA:public}")
                .contains("spring.flyway.default-schema=${APP_DATABASE_SCHEMA:public}")
                .contains("spring.flyway.schemas=${APP_DATABASE_SCHEMA:public}")
                .contains("spring.flyway.create-schemas=true")
                .contains("spring.flyway.baseline-on-migrate=true")
                .doesNotContain("render-data.sql");
        assertThat(baselineMigration).isRegularFile();
        assertThat(Files.readString(baselineMigration))
                .contains("CREATE TABLE")
                .doesNotContain("Demo123!")
                .doesNotContain("demo_admin");
        assertThat(chatThreadMigration).isRegularFile();
        assertThat(Files.readString(chatThreadMigration))
                .contains("ALTER TABLE chat_threads")
                .contains("ADD COLUMN chat_type VARCHAR(32) NOT NULL DEFAULT 'AI_PERSONAL'")
                .contains("ADD COLUMN peer_user_id BIGINT NULL");
        assertThat(healthRecordMigration).isRegularFile();
        assertThat(Files.readString(healthRecordMigration))
                .contains("ALTER TABLE health_records")
                .contains("ALTER COLUMN bmi TYPE DOUBLE PRECISION")
                .contains("ALTER COLUMN systolic_blood_pressure TYPE INTEGER");
        assertThat(savedPaymentMigration).isRegularFile();
        assertThat(Files.readString(savedPaymentMigration))
                .contains("ALTER TABLE saved_payment_methods")
                .contains("ALTER COLUMN last_four TYPE VARCHAR(4)");
        assertThat(RESOURCES.resolve("render-data.sql")).doesNotExist();
    }

    @Test
    void uploadServingUsesEveryConfiguredStorageBoundary() throws IOException {
        String webConfig = Files.readString(Path.of(
                "src/main/java/uk/ac/cf/_5/group14/One_To_One/Security/WebConfig.java"));

        assertThat(webConfig)
                .contains("${app.storage.profile-dir:uploads/profile}")
                .contains("${app.storage.chat-dir:uploads/chat}")
                .contains("${app.storage.merch-dir:uploads/merch}")
                .contains("${app.storage.workout-video-dir:uploads/workout-videos}")
                .doesNotContain(".addResourceLocations(\"file:uploads/\")");
    }

    @Test
    void stagingBlueprintUsesDedicatedSchemaDurableStorageAndSafeProviders() throws IOException {
        Path blueprintPath = Path.of("../render-staging.yaml");
        String blueprint = Files.readString(blueprintPath);
        Map<?, ?> parsed = new Yaml().load(blueprint);

        assertThat(parsed.containsKey("services")).isTrue();
        assertThat(parsed.containsKey("databases")).isFalse();
        assertThat(blueprint)
                .contains("name: one-to-one-staging-jhuds")
                .contains("plan: starter")
                .contains("region: oregon")
                .contains("branch: James/phase4-staging-readiness")
                .contains("mountPath: /var/data/uploads")
                .contains("autoDeploy: false")
                .containsPattern("key: APP_DATABASE_SCHEMA\\R\\s+value: one_to_one_staging")
                .containsPattern("key: APP_EMAIL_PROVIDER\\R\\s+value: none")
                .containsPattern("key: APP_SMS_PROVIDER\\R\\s+value: console")
                .containsPattern("key: STRIPE_SECRET_KEY\\R\\s+value: \"\"")
                .containsPattern("key: DATABASE_URL\\R\\s+sync: false")
                .doesNotContain("APP_DATABASE_SCHEMA: public")
                .doesNotContain("one-to-one-web");
    }
}
