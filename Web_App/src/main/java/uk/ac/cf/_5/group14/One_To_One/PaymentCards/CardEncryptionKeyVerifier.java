package uk.ac.cf._5.group14.One_To_One.PaymentCards;

import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.ApplicationArguments;
import org.springframework.boot.ApplicationRunner;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.stereotype.Component;
import org.springframework.transaction.annotation.Transactional;

import java.sql.Timestamp;
import java.time.Instant;
import java.util.List;

/**
 * Fails startup when the configured card key cannot decrypt the persistent
 * continuity marker or any saved provider token.
 */
@Component
@Slf4j
public class CardEncryptionKeyVerifier implements ApplicationRunner {

    static final short MARKER_ID = 1;
    static final String MARKER_PLAINTEXT = "one-to-one-card-key-check:v1";

    private final JdbcTemplate jdbcTemplate;
    private final CardEncryptionService cardEncryptionService;
    private final boolean persistentKeyRequired;

    public CardEncryptionKeyVerifier(
            JdbcTemplate jdbcTemplate,
            CardEncryptionService cardEncryptionService,
            @Value("${app.encryption.require-persistent-key:false}")
            boolean persistentKeyRequired) {
        this.jdbcTemplate = jdbcTemplate;
        this.cardEncryptionService = cardEncryptionService;
        this.persistentKeyRequired = persistentKeyRequired;
    }

    @Override
    @Transactional
    public void run(ApplicationArguments args) {
        if (!persistentKeyRequired) {
            return;
        }

        String marker = loadMarker();
        if (marker == null) {
            try {
                jdbcTemplate.update(
                        """
                        INSERT INTO card_encryption_key_checks
                            (id, encrypted_marker, created_at, verified_at)
                        VALUES (?, ?, ?, ?)
                        """,
                        MARKER_ID,
                        cardEncryptionService.encrypt(MARKER_PLAINTEXT),
                        Timestamp.from(Instant.now()),
                        Timestamp.from(Instant.now()));
            } catch (DuplicateKeyException concurrentInitialisation) {
                // Another instance created the marker. Load and verify it below.
            }
            marker = loadMarker();
        }

        if (marker == null
                || !MARKER_PLAINTEXT.equals(cardEncryptionService.decrypt(marker))) {
            throw new IllegalStateException(
                    "Persistent card encryption key continuity check failed");
        }

        List<String> encryptedProviderTokens = jdbcTemplate.queryForList(
                """
                SELECT provider_payment_method_id
                  FROM saved_payment_methods
                 WHERE provider_payment_method_id IS NOT NULL
                   AND provider_payment_method_id <> ''
                """,
                String.class);
        for (String encryptedProviderToken : encryptedProviderTokens) {
            String providerToken = cardEncryptionService.decrypt(encryptedProviderToken);
            if (providerToken.isBlank()) {
                throw new IllegalStateException(
                        "A saved payment method decrypted to an empty provider token");
            }
        }

        jdbcTemplate.update(
                "UPDATE card_encryption_key_checks SET verified_at = ? WHERE id = ?",
                Timestamp.from(Instant.now()),
                MARKER_ID);
        log.info(
                "Persistent card encryption key continuity verified for {} saved payment method(s)",
                encryptedProviderTokens.size());
    }

    private String loadMarker() {
        List<String> markers = jdbcTemplate.queryForList(
                "SELECT encrypted_marker FROM card_encryption_key_checks WHERE id = ?",
                String.class,
                MARKER_ID);
        return markers.isEmpty() ? null : markers.getFirst();
    }
}
