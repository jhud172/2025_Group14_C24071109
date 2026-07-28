package uk.ac.cf._5.group14.One_To_One;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class OneToOneApplicationDatabaseSchemaTest {

    @Test
    void appendsConfiguredSchemaToPostgresUrl() {
        assertThat(OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one",
                "one_to_one_staging"))
                .isEqualTo("jdbc:postgresql://db.internal/one_to_one?currentSchema=one_to_one_staging");
    }

    @Test
    void preservesExistingParametersAndSchema() {
        assertThat(OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one?sslmode=require",
                "one_to_one_staging"))
                .isEqualTo(
                        "jdbc:postgresql://db.internal/one_to_one?sslmode=require&currentSchema=one_to_one_staging");

        assertThat(OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one?currentSchema=one_to_one_staging",
                "one_to_one_staging"))
                .isEqualTo(
                        "jdbc:postgresql://db.internal/one_to_one?currentSchema=one_to_one_staging");
    }

    @Test
    void rejectsUnsafeSchemaIdentifiers() {
        assertThatThrownBy(() -> OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one",
                "public; DROP SCHEMA public"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("valid PostgreSQL identifier");

        assertThatThrownBy(() -> OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one?currentSchema=public",
                "one_to_one_staging"))
                .isInstanceOf(IllegalArgumentException.class)
                .hasMessageContaining("conflicts");
    }

    @Test
    void leavesNonPostgresAndUnconfiguredUrlsUnchanged() {
        assertThat(OneToOneApplication.applyDatabaseSchema(
                "jdbc:h2:mem:testdb",
                "one_to_one_staging"))
                .isEqualTo("jdbc:h2:mem:testdb");
        assertThat(OneToOneApplication.applyDatabaseSchema(
                "jdbc:postgresql://db.internal/one_to_one",
                ""))
                .isEqualTo("jdbc:postgresql://db.internal/one_to_one");
    }
}
