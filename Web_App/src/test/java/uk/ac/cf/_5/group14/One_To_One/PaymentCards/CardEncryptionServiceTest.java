package uk.ac.cf._5.group14.One_To_One.PaymentCards;

import org.junit.jupiter.api.Test;

import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Base64;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class CardEncryptionServiceTest {

    private static final String KEY = Base64.getEncoder().encodeToString(
            Arrays.copyOf(
                    "phase4-persistent-card-key".getBytes(StandardCharsets.UTF_8),
                    32
            )
    );

    @Test
    void decryptsAcrossServiceInstancesWithTheSamePersistentKey() {
        CardEncryptionService first = new CardEncryptionService(KEY, "2bd", false, true);
        String encrypted = first.encrypt("pm_test_restart_4242");

        CardEncryptionService afterRestart =
                new CardEncryptionService(KEY, "2bd", false, true);

        assertThat(encrypted).startsWith("v1:");
        assertThat(encrypted).doesNotContain("pm_test_restart_4242");
        assertThat(afterRestart.decrypt(encrypted)).isEqualTo("pm_test_restart_4242");
    }

    @Test
    void decryptsLegacyUnversionedCiphertext() {
        CardEncryptionService service = new CardEncryptionService(KEY, "2bd", false, true);
        String versioned = service.encrypt("pm_test_legacy");

        assertThat(service.decrypt(versioned.substring("v1:".length())))
                .isEqualTo("pm_test_legacy");
    }

    @Test
    void requiredPersistentKeyFailsClosedWhenMissing() {
        assertThatThrownBy(() -> new CardEncryptionService("", "2bd", false, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("required");
    }

    @Test
    void rejectsInvalidOrWrongLengthKeys() {
        assertThatThrownBy(() -> new CardEncryptionService("not base64!", "2bd", false, true))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("valid Base64");
        assertThatThrownBy(() -> new CardEncryptionService(
                Base64.getEncoder().encodeToString(new byte[16]),
                "2bd",
                false,
                true
        ))
                .isInstanceOf(IllegalStateException.class)
                .hasMessageContaining("32-byte");
    }
}
