package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;

class PublicPageConsistencyContractTest {

    @Test
    void longPublicPagesUseACompactFourStageJourney() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(count(home, "<section")).isEqualTo(4);
        assertThat(count(about, "<section")).isEqualTo(4);
        assertThat(count(pricing, "<section") + count(pricing, "<header")).isEqualTo(4);

        assertThat(home).doesNotContain("Support feedback", "Precision Engineered", "Cycle of Progress");
        assertThat(about).doesNotContain("Platform Features", "Personalized", "Programs", "centralized");
        assertThat(pricing).doesNotContain("Everything Premium Unlocks", "Frequently Asked Questions");
    }

    @Test
    void publicRoutesRetainTheirFunctionalDestinations() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String faq = read("src/main/resources/templates/public-views/public/faq.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(home)
                .contains("th:href=\"@{/signup}\"")
                .contains("th:href=\"@{/explore}\"")
                .contains("th:href=\"@{/support}\"");
        assertThat(about).contains("th:href=\"@{/signup}\"").contains("th:href=\"@{/explore}\"");
        assertThat(faq).contains("id=\"faq-search\"").contains("data-faq-item").contains("th:href=\"@{/support}\"");
        assertThat(pricing)
                .contains("th:each=\"plan : ${plans}\"")
                .contains("@{/pricing/checkout(plan=${plan.plan})}")
                .contains("pricingSuccess")
                .contains("paymentProviderConfigured");
    }

    @Test
    void publicSectionMotionUsesExistingTokensAndAccessibleFallbacks() throws IOException {
        String css = read("src/main/resources/static/css/components/misc/public-sections.css");
        String script = read("src/main/resources/static/js/public/public-sections.js");
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String about = read("src/main/resources/templates/public-views/public/about.html");
        String faq = read("src/main/resources/templates/public-views/public/faq.html");
        String pricing = read("src/main/resources/templates/public-views/payments/pricing.html");

        assertThat(css)
                .contains("--motion-duration-route")
                .contains("--motion-ease-enter")
                .contains("prefers-reduced-motion")
                .doesNotContain("transition: all");
        assertThat(script)
                .contains("IntersectionObserver")
                .contains("prefers-reduced-motion")
                .contains("observer.unobserve");
        assertThat(home).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(about).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(faq).contains("/js/public/public-sections.js(v=${assetVersion})");
        assertThat(pricing).contains("/js/public/public-sections.js(v=${assetVersion})");
    }

    private static int count(String source, String needle) {
        return (source.length() - source.replace(needle, "").length()) / needle.length();
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
