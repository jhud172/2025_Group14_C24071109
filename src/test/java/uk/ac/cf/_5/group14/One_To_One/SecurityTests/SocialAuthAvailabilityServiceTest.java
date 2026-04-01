package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.ObjectProvider;
import org.springframework.mock.env.MockEnvironment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import uk.ac.cf._5.group14.One_To_One.Security.SocialAuth.SocialAuthAvailabilityService;

import java.util.Objects;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.groups.Tuple.tuple;
import static org.mockito.BDDMockito.given;
import static org.mockito.Mockito.mock;

class SocialAuthAvailabilityServiceTest {

    @Test
    void microsoftIsDisabledWhenAnyRequiredPropertyIsFalse() {
        ClientRegistrationRepository repository = mock(ClientRegistrationRepository.class);
        given(repository.findByRegistrationId("microsoft")).willReturn(buildRegistration("microsoft"));

        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.microsoft-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-id", "false")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-secret", "secret")
            .withProperty("spring.security.oauth2.client.provider.microsoft.issuer-uri", "https://login.microsoftonline.com/common/v2.0");

        SocialAuthAvailabilityService service = new SocialAuthAvailabilityService(objectProvider(repository), environment);

        assertThat(service.isProviderVisible("microsoft")).isTrue();
        assertThat(service.isProviderEnabled("microsoft")).isFalse();
        assertThat(service.getVisibleProviders()).extracting("id", "enabled")
            .containsExactly(tuple("microsoft", false));
    }

    @Test
    void appleIsDisabledWhenSecretIsFalse() {
        ClientRegistrationRepository repository = mock(ClientRegistrationRepository.class);
        given(repository.findByRegistrationId("apple")).willReturn(buildRegistration("apple"));

        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.apple-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.apple.client-id", "client-id")
            .withProperty("spring.security.oauth2.client.registration.apple.client-secret", "false")
            .withProperty("spring.security.oauth2.client.provider.apple.issuer-uri", "https://appleid.apple.com");

        SocialAuthAvailabilityService service = new SocialAuthAvailabilityService(objectProvider(repository), environment);

        assertThat(service.isProviderVisible("apple")).isTrue();
        assertThat(service.isProviderEnabled("apple")).isFalse();
        assertThat(service.getVisibleProviders()).extracting("id", "enabled")
            .containsExactly(tuple("apple", false));
    }

    @Test
    void microsoftIsDisabledWhenIssuerUriIsNotAbsolute() {
        ClientRegistrationRepository repository = mock(ClientRegistrationRepository.class);
        given(repository.findByRegistrationId("microsoft")).willReturn(buildRegistration("microsoft"));

        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.microsoft-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-id", "client-id")
            .withProperty("spring.security.oauth2.client.registration.microsoft.client-secret", "secret")
            .withProperty("spring.security.oauth2.client.provider.microsoft.issuer-uri", "false");

        SocialAuthAvailabilityService service = new SocialAuthAvailabilityService(objectProvider(repository), environment);

        assertThat(service.isProviderVisible("microsoft")).isTrue();
        assertThat(service.isProviderEnabled("microsoft")).isFalse();
        assertThat(service.hasEnabledProviders()).isFalse();
    }

    @Test
    void googleRemainsEnabledWhenFlagIsTrueAndRequiredPropertiesArePresent() {
        ClientRegistrationRepository repository = mock(ClientRegistrationRepository.class);
        given(repository.findByRegistrationId("google")).willReturn(buildRegistration("google"));

        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.google-login-active", "true")
            .withProperty("spring.security.oauth2.client.registration.google.client-id", "client-id")
            .withProperty("spring.security.oauth2.client.registration.google.client-secret", "client-secret");

        SocialAuthAvailabilityService service = new SocialAuthAvailabilityService(objectProvider(repository), environment);

        assertThat(service.isProviderVisible("google")).isTrue();
        assertThat(service.isProviderEnabled("google")).isTrue();
        assertThat(service.getVisibleProviders()).extracting("id", "enabled")
            .containsExactly(tuple("google", true));
    }

    @Test
    void googleIsDisabledWhenActivationFlagIsFalseEvenWithCredentialsPresent() {
        ClientRegistrationRepository repository = mock(ClientRegistrationRepository.class);
        given(repository.findByRegistrationId("google")).willReturn(buildRegistration("google"));

        MockEnvironment environment = new MockEnvironment()
            .withProperty("app.oauth.google-login-active", "false")
            .withProperty("spring.security.oauth2.client.registration.google.client-id", "client-id")
            .withProperty("spring.security.oauth2.client.registration.google.client-secret", "client-secret");

        SocialAuthAvailabilityService service = new SocialAuthAvailabilityService(objectProvider(repository), environment);

        assertThat(service.isProviderVisible("google")).isFalse();
        assertThat(service.isProviderEnabled("google")).isFalse();
        assertThat(service.getVisibleProviders()).isEmpty();
    }

    private static ObjectProvider<ClientRegistrationRepository> objectProvider(ClientRegistrationRepository repository) {
        return new ObjectProvider<>() {
            @Override
            public ClientRegistrationRepository getObject(Object... args) {
                return repository;
            }

            @Override
            public ClientRegistrationRepository getIfAvailable() {
                return repository;
            }

            @Override
            public ClientRegistrationRepository getIfUnique() {
                return repository;
            }
        };
    }

    private static ClientRegistration buildRegistration(String registrationId) {
        return ClientRegistration.withRegistrationId(registrationId)
            .clientId("client-id")
            .clientSecret("client-secret")
            .authorizationGrantType(org.springframework.security.oauth2.core.AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri("{baseUrl}/login/oauth2/code/{registrationId}")
            .scope("openid", "email", "profile")
            .authorizationUri("https://example.com/oauth2/authorize")
            .tokenUri("https://example.com/oauth2/token")
            .userInfoUri("https://example.com/userinfo")
            .userNameAttributeName("sub")
            .clientName(Objects.requireNonNullElse(registrationId, "provider"))
            .build();
    }
}
