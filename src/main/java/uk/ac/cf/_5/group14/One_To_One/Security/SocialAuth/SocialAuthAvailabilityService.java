package uk.ac.cf._5.group14.One_To_One.Security.SocialAuth;

import org.springframework.beans.factory.ObjectProvider;
import org.springframework.core.env.Environment;
import org.springframework.security.oauth2.client.registration.ClientRegistration;
import org.springframework.security.oauth2.client.registration.ClientRegistrationRepository;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Map;

@Service
public class SocialAuthAvailabilityService {

    private static final List<String> SUPPORTED_PROVIDERS = List.of("google", "microsoft", "apple");
    private static final Map<String, String> ACTIVATION_PROPERTIES = Map.of(
        "google", "app.oauth.google-login-active",
        "microsoft", "app.oauth.microsoft-login-active",
        "apple", "app.oauth.apple-login-active"
    );
    private static final Map<String, List<String>> REQUIRED_PROPERTIES = Map.of(
        "google", List.of(
            "spring.security.oauth2.client.registration.google.client-id",
            "spring.security.oauth2.client.registration.google.client-secret"
        ),
        "microsoft", List.of(
            "spring.security.oauth2.client.registration.microsoft.client-id",
            "spring.security.oauth2.client.registration.microsoft.client-secret",
            "spring.security.oauth2.client.provider.microsoft.issuer-uri"
        ),
        "apple", List.of(
            "spring.security.oauth2.client.registration.apple.client-id",
            "spring.security.oauth2.client.registration.apple.client-secret",
            "spring.security.oauth2.client.provider.apple.issuer-uri"
        )
    );

    private final ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider;
    private final Environment environment;

    public SocialAuthAvailabilityService(ObjectProvider<ClientRegistrationRepository> clientRegistrationRepositoryProvider,
                                         Environment environment) {
        this.clientRegistrationRepositoryProvider = clientRegistrationRepositoryProvider;
        this.environment = environment;
    }

    public List<SocialProviderOption> getVisibleProviders() {
        return SUPPORTED_PROVIDERS.stream()
            .filter(this::isProviderVisible)
            .map(provider -> new SocialProviderOption(provider, providerLabel(provider), isProviderEnabled(provider)))
            .toList();
    }

    public boolean isProviderEnabled(String provider) {
        ClientRegistrationRepository repository = clientRegistrationRepositoryProvider.getIfAvailable();
        if (repository == null || provider == null || provider.isBlank()) {
            return false;
        }
        String normalizedProvider = provider.trim().toLowerCase();
        ClientRegistration registration = repository.findByRegistrationId(normalizedProvider);
        return registration != null
            && isActivationEnabled(normalizedProvider)
            && hasUsableConfiguration(normalizedProvider);
    }

    public boolean isProviderVisible(String provider) {
        if (provider == null || provider.isBlank()) {
            return false;
        }
        return isActivationEnabled(provider.trim().toLowerCase());
    }

    private boolean isActivationEnabled(String provider) {
        String property = ACTIVATION_PROPERTIES.get(provider);
        if (property == null) {
            return false;
        }
        return Boolean.parseBoolean(environment.getProperty(property, "false"));
    }

    private boolean hasUsableConfiguration(String provider) {
        return REQUIRED_PROPERTIES.getOrDefault(provider, List.of()).stream()
            .allMatch(this::hasUsableValue);
    }

    private String providerLabel(String provider) {
        return switch (provider) {
            case "google" -> "Google";
            case "microsoft" -> "Microsoft";
            case "apple" -> "Apple";
            default -> provider;
        };
    }

    private boolean hasUsableValue(String property) {
        String value = environment.getProperty(property);
        if (value == null) {
            return false;
        }

        String trimmed = value.trim();
        return !trimmed.isEmpty() && !"false".equalsIgnoreCase(trimmed);
    }
}
