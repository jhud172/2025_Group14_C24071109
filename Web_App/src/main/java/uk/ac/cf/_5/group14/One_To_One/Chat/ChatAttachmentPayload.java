package uk.ac.cf._5.group14.One_To_One.Chat;

/**
 * Minimal attachment metadata used by the Charlie popup chat.
 */
public record ChatAttachmentPayload(
        String url,
        String fileName,
        String contentType
) {
}
