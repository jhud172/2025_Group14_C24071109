package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import uk.ac.cf._5.group14.One_To_One.Security.SocialAuth.SocialAuthClientRegistrationConfig;

import java.util.ArrayList;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class SocialAuthClientRegistrationConfigTest {

    private final SocialAuthClientRegistrationConfig config = new SocialAuthClientRegistrationConfig();

    @Test
    void invalidMicrosoftIssuerDoesNotCreateRegistration() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.microsoft-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-id", "client-id")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-secret", "client-secret")
            .withProperty("spring.security.oauth2.client.provider.microsoft.issuer-uri", "false");

        ClientRegistrationRepository repository = config.clientRegistrationRepository(environment);

        assertThat(repository.findByRegistrationId("microsoft")).isNull();
        assertThat(registrations(repository)).isEmpty();
    }

    @Test
    void activeGoogleProviderIsRegisteredWithoutOtherBrokenProviders() {
        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.google-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.google.client-id", "google-client-id")
            .withProperty("spring.security.oauth2.client.registration.google.client-secret", "google-client-secret")
            .withProperty("app.oauth.microsoft-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-id", "microsoft-client-id")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-secret", "microsoft-client-secret")
            .withProperty("spring.security.oauth2.client.provider.microsoft.issuer-uri", "false");

        ClientRegistrationRepository repository = config.clientRegistrationRepository(environment);

        assertThat(registrations(repository))
            .extracting(ClientRegistration::getRegistrationId)
            .containsExactly("google");
    }

    private static List<ClientRegistration> registrations(ClientRegistrationRepository repository) {
        List<ClientRegistration> registrations = new ArrayList<>();
        if (repository instanceof Iterable<?> iterable) {
            for (Object entry : iterable) {
                registrations.add((ClientRegistration) entry);
            }
        }
        return registrations;
    }
}
