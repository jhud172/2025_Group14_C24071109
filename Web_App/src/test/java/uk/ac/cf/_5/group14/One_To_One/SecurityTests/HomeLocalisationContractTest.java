package uk.ac.cf._5.group14.One_To_One.SecurityTests;

import org.junit.jupiter.api.Test;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

class HomeLocalisationContractTest {

    @Test
    void englishAndWelshHomeBundlesHaveTheSameOrderedKeys() throws IOException {
        List<String> englishKeys = keys("src/main/resources/messages-home.properties");
        List<String> welshKeys = keys("src/main/resources/messages-home_cy.properties");

        assertThat(englishKeys)
                .hasSize(295)
                .containsExactlyElementsOf(welshKeys);
        assertThat(englishKeys.getFirst()).isEqualTo("home.001.page.title");
        assertThat(englishKeys.getLast()).isEqualTo("home.294.to.top");
    }

    @Test
    void homeAndSharedShellResolveCopyFromMessageBundles() throws IOException {
        String home = read("src/main/resources/templates/public-views/home/public.html");
        String base = read("src/main/resources/templates/base.html");
        String navbar = read("src/main/resources/templates/universal-fragments/layout/navbar.html");
        String selector = read("src/main/resources/templates/universal-fragments/layout/language-selector.html");
        String application = read("src/main/resources/application.properties");
        String script = read("src/main/resources/static/js/public/public-page.js");

        assertThat(home)
                .contains("layout(#{home.001.page.title}")
                .contains("#{home.009.hero.action.join}")
                .contains("#{home.257.workspaces.trainer.action}")
                .contains("#{home.272.workspaces.gym.action}")
                .contains("data-i18n-note-required")
                .contains("#{home.294.to.top}");
        assertThat(base)
                .contains("lang=${#locale.language}")
                .contains("data-language=${#locale.language}");
        assertThat(navbar)
                .contains("language-selector :: languageSelector")
                .contains("#{nav.development}");
        assertThat(selector)
                .contains("name=\"lang\"")
                .contains("value=\"en\"")
                .contains("value=\"cy\"");
        assertThat(application).contains("spring.messages.basename=messages,messages-home");
        assertThat(script)
                .contains("'i18nSessionFinished'")
                .contains("formatTranslated");
    }

    private static List<String> keys(String relativePath) throws IOException {
        return Files.readAllLines(Path.of(relativePath)).stream()
                .map(String::trim)
                .filter(line -> line.startsWith("home."))
                .map(line -> line.substring(0, line.indexOf('=')).trim())
                .toList();
    }

    private static String read(String relativePath) throws IOException {
        return Files.readString(Path.of(relativePath));
    }
}
