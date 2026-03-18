package uk.ac.cf._5.group14.One_To_One.PaymentCards;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.Cipher;
import javax.crypto.SecretKey;
import javax.crypto.spec.GCMParameterSpec;
import javax.crypto.spec.SecretKeySpec;
import java.nio.charset.StandardCharsets;
import java.security.SecureRandom;
import java.util.Base64;
import java.util.logging.Logger;

/**
 * AES-256-GCM encryption for sensitive card data.
 * The key is read from the {@code app.encryption.card-key} property (32-byte base64-encoded secret).
 * If no key is configured a random in-memory key is used (cards will not survive restarts).
 */
@Component
public class CardEncryptionService {

    private static final String ALGORITHM = "AES/GCM/NoPadding";
    private static final int GCM_IV_LENGTH = 12;
    private static final int GCM_TAG_LENGTH = 128;
    private static final Logger log = Logger.getLogger(CardEncryptionService.class.getName());

    private final SecretKey secretKey;

    public CardEncryptionService(
            @Value("${app.encryption.card-key:}") String base64Key) {
        if (base64Key != null && !base64Key.isBlank()) {
            byte[] keyBytes = Base64.getDecoder().decode(base64Key.trim());
            if (keyBytes.length != 32) {
                throw new IllegalStateException(
                        "app.encryption.card-key must be a 32-byte (256-bit) Base64-encoded key");
            }
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        } else {
            // Fallback: ephemeral random key (dev / test only)
            log.warning("SECURITY WARNING: app.encryption.card-key is not configured. " +
                    "Using an ephemeral in-memory AES key â€“ encrypted card data will NOT survive restarts. " +
                    "Set app.encryption.card-key to a 32-byte base64-encoded key in production.");
            byte[] keyBytes = new byte[32];
            new SecureRandom().nextBytes(keyBytes);
            this.secretKey = new SecretKeySpec(keyBytes, "AES");
        }
    }

    /**
     * Encrypts {@code plaintext} and returns a base64-encoded string:
     * {@code <iv_base64>:<ciphertext_base64>}.
     */
    public String encrypt(String plaintext) {
        try {
            byte[] iv = new byte[GCM_IV_LENGTH];
            new SecureRandom().nextBytes(iv);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.ENCRYPT_MODE, secretKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] ciphertext = cipher.doFinal(plaintext.getBytes(StandardCharsets.UTF_8));

            return Base64.getEncoder().encodeToString(iv)
                    + ":" + Base64.getEncoder().encodeToString(ciphertext);
        } catch (Exception e) {
            throw new IllegalStateException("Card encryption failed", e);
        }
    }

    /**
     * Decrypts a token previously produced by {@link #encrypt(String)}.
     */
    public String decrypt(String token) {
        try {
            String[] parts = token.split(":", 2);
            if (parts.length != 2) {
                throw new IllegalArgumentException("Invalid encrypted token format");
            }
            byte[] iv = Base64.getDecoder().decode(parts[0]);
            byte[] ciphertext = Base64.getDecoder().decode(parts[1]);

            Cipher cipher = Cipher.getInstance(ALGORITHM);
            cipher.init(Cipher.DECRYPT_MODE, secretKey,
                    new GCMParameterSpec(GCM_TAG_LENGTH, iv));

            byte[] plaintext = cipher.doFinal(ciphertext);
            return new String(plaintext, StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new IllegalStateException("Card decryption failed", e);
        }
    }
}
