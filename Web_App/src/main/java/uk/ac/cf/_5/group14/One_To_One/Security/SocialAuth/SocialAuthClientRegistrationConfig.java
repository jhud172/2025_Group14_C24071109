package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.env.Environment;
import org.springframework.security.config.oauth2.client.CommonOAuth2Provider;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.security.oauth2.core.AuthorizationGrantType;
import org.springframework.security.oauth2.core.ClientAuthenticationMethod;

import java.net.URI;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Configuration(proxyBeanMethods = false)
public class SocialAuthClientRegistrationConfig {

    private static final Pattern MICROSOFT_ISSUER_PATTERN =
        Pattern.compile("^(https://login\\.microsoftonline\\.com/[^/]+)/v2\\.0/?$");

    @Bean
    public ClientRegistrationRepository clientRegistrationRepository(Environment environment) {
        List<ClientRegistration> registrations = new ArrayList<>();

        buildGoogleRegistration(environment).ifPresent(registrations::add);
        buildMicrosoftRegistration(environment).ifPresent(registrations::add);
        buildAppleRegistration(environment).ifPresent(registrations::add);

        return new StaticClientRegistrationRepository(registrations);
    }

    private Optional<ClientRegistration> buildGoogleRegistration(Environment environment) {
        if (!isProviderActive(environment, "app.oauth.google-login-active")) {
            return Optional.empty();
        }

        String clientId = requiredValue(environment, "spring.security.oauth2.client.registration.google.client-id");
        String clientSecret = requiredValue(environment, "spring.security.oauth2.client.registration.google.client-secret");
        if (clientId == null || clientSecret == null) {
            return Optional.empty();
        }

        ClientRegistration registration = CommonOAuth2Provider.GOOGLE.getBuilder("google")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .scope(scopes(environment, "spring.security.oauth2.client.registration.google.scope", "openid,profile,email"))
            .build();

        return Optional.of(registration);
    }

    private Optional<ClientRegistration> buildMicrosoftRegistration(Environment environment) {
        if (!isProviderActive(environment, "app.oauth.microsoft-login-active")) {
            return Optional.empty();
        }

        String clientId = requiredValue(environment, "spring.security.oauth2.client.registration.microsoft.client-id");
        String clientSecret = requiredValue(environment, "spring.security.oauth2.client.registration.microsoft.client-secret");
        String issuerUri = absoluteUriValue(environment, "spring.security.oauth2.client.provider.microsoft.issuer-uri");
        if (clientId == null || clientSecret == null || issuerUri == null) {
            return Optional.empty();
        }

        String authorityBase = resolveMicrosoftAuthorityBase(issuerUri);
        ClientRegistration registration = ClientRegistration.withRegistrationId("microsoft")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_BASIC)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(environment.getProperty(
                "spring.security.oauth2.client.registration.microsoft.redirect-uri",
                "{baseUrl}/login/oauth2/code/{registrationId}"
            ))
            .scope(scopes(environment, "spring.security.oauth2.client.registration.microsoft.scope", "openid,profile,email"))
            .authorizationUri(authorityBase + "/oauth2/v2.0/authorize")
            .tokenUri(authorityBase + "/oauth2/v2.0/token")
            .jwkSetUri(authorityBase + "/discovery/v2.0/keys")
            .issuerUri(issuerUri)
            .userInfoUri("https://graph.microsoft.com/oidc/userinfo")
            .userNameAttributeName("sub")
            .clientName(environment.getProperty(
                "spring.security.oauth2.client.registration.microsoft.client-name",
                "Microsoft"
            ))
            .build();

        return Optional.of(registration);
    }

    private Optional<ClientRegistration> buildAppleRegistration(Environment environment) {
        if (!isProviderActive(environment, "app.oauth.apple-login-active")) {
            return Optional.empty();
        }

        String clientId = requiredValue(environment, "spring.security.oauth2.client.registration.apple.client-id");
        String clientSecret = requiredValue(environment, "spring.security.oauth2.client.registration.apple.client-secret");
        String issuerUri = absoluteUriValue(environment, "spring.security.oauth2.client.provider.apple.issuer-uri");
        if (clientId == null || clientSecret == null || issuerUri == null) {
            return Optional.empty();
        }

        ClientRegistration registration = ClientRegistration.withRegistrationId("apple")
            .clientId(clientId)
            .clientSecret(clientSecret)
            .clientAuthenticationMethod(ClientAuthenticationMethod.CLIENT_SECRET_POST)
            .authorizationGrantType(AuthorizationGrantType.AUTHORIZATION_CODE)
            .redirectUri(environment.getProperty(
                "spring.security.oauth2.client.registration.apple.redirect-uri",
                "{baseUrl}/login/oauth2/code/{registrationId}"
            ))
            .scope(scopes(environment, "spring.security.oauth2.client.registration.apple.scope", "openid,email,name"))
            .authorizationUri("https://appleid.apple.com/auth/authorize")
            .tokenUri("https://appleid.apple.com/auth/token")
            .jwkSetUri("https://appleid.apple.com/auth/keys")
            .issuerUri(issuerUri)
            .userNameAttributeName("sub")
            .clientName(environment.getProperty(
                "spring.security.oauth2.client.registration.apple.client-name",
                "Apple"
            ))
            .build();

        return Optional.of(registration);
    }

    private boolean isProviderActive(Environment environment, String property) {
        return Boolean.parseBoolean(environment.getProperty(property, "false"));
    }

    private String requiredValue(Environment environment, String property) {
        String value = environment.getProperty(property);
        if (value == null) {
            return null;
        }

        String trimmed = value.trim();
        if (trimmed.isEmpty() || "false".equalsIgnoreCase(trimmed)) {
            return null;
        }

        return trimmed;
    }

    private String absoluteUriValue(Environment environment, String property) {
        String value = requiredValue(environment, property);
        if (value == null) {
            return null;
        }

        try {
            URI uri = URI.create(value);
            return uri.isAbsolute() ? value : null;
        } catch (IllegalArgumentException ex) {
            return null;
        }
    }

    private String[] scopes(Environment environment, String property, String fallback) {
        String raw = environment.getProperty(property, fallback);
        return raw == null
            ? new String[0]
            : Arrays.stream(raw.split(","))
                .map(String::trim)
                .filter(scope -> !scope.isEmpty())
                .toArray(String[]::new);
    }

    private String resolveMicrosoftAuthorityBase(String issuerUri) {
        Matcher matcher = MICROSOFT_ISSUER_PATTERN.matcher(issuerUri);
        if (matcher.matches()) {
            return matcher.group(1);
        }

        return "https://login.microsoftonline.com/common";
    }

    private static final class StaticClientRegistrationRepository
        implements ClientRegistrationRepository, Iterable<ClientRegistration> {

        private final List<ClientRegistration> registrations;
        private final Map<String, ClientRegistration> registrationsById;

        private StaticClientRegistrationRepository(List<ClientRegistration> registrations) {
            this.registrations = List.copyOf(registrations);
            this.registrationsById = this.registrations.stream()
                .collect(Collectors.toUnmodifiableMap(ClientRegistration::getRegistrationId, registration -> registration));
        }

        @Override
        public ClientRegistration findByRegistrationId(String registrationId) {
            return registrationId == null ? null : registrationsById.get(registrationId);
        }

        @Override
        public Iterator<ClientRegistration> iterator() {
            return registrations.iterator();
        }
    }
}
