package uk.ac.cf._5.group14.One_To_One.Users;

public enum SocialAuthProvider {
    GOOGLE,
    MICROSOFT,
    APPLE;

    public static SocialAuthProvider fromRegistrationId(String registrationId) {
        if (registrationId == null || registrationId.isBlank()) {
            throw new IllegalArgumentException("Registration ID is required.");
        }
        return SocialAuthProvider.valueOf(registrationId.trim().toUpperCase());
    }
}
