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

        assertThat(renderProperties)
                .contains("spring.sql.init.mode=never")
                .contains("spring.flyway.enabled=true")
                .contains("spring.flyway.locations=classpath:db/migration/postgresql")
                .contains("spring.flyway.baseline-on-migrate=true")
                .doesNotContain("render-data.sql");
        assertThat(baselineMigration).isRegularFile();
        assertThat(Files.readString(baselineMigration))
                .contains("CREATE TABLE")
                .doesNotContain("Demo123!")
                .doesNotContain("demo_admin");
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
    void stagingBlueprintIsIsolatedDurableAndSafeByDefault() throws IOException {
        Path blueprintPath = Path.of("../render-staging.yaml");
        String blueprint = Files.readString(blueprintPath);
        Map<?, ?> parsed = new Yaml().load(blueprint);

        assertThat(parsed.containsKey("services")).isTrue();
        assertThat(parsed.containsKey("databases")).isTrue();
        assertThat(blueprint)
                .contains("name: one-to-one-staging-jhuds")
                .contains("name: one-to-one-staging-db")
                .contains("plan: starter")
                .contains("plan: basic-256mb")
                .contains("branch: James/phase4-staging-readiness")
                .contains("mountPath: /var/data/uploads")
                .contains("ipAllowList: []")
                .contains("autoDeploy: false")
                .containsPattern("key: APP_EMAIL_PROVIDER\\R\\s+value: none")
                .containsPattern("key: APP_SMS_PROVIDER\\R\\s+value: console")
                .containsPattern("key: STRIPE_SECRET_KEY\\R\\s+value: \"\"")
                .doesNotContain("sync: false")
                .doesNotContain("one-to-one-web");
    }
}
