package uk.ac.cf._5.group14.One_To_One.ConfigTests;

import org.junit.jupiter.api.Test;
import uk.ac.cf._5.group14.One_To_One.Config.SupportedLanguage;

import static org.assertj.core.api.Assertions.assertThat;

class SupportedLanguageTest {

    @Test
    void exposesAllFourteenLanguagesInDisplayOrder() {
        assertThat(SupportedLanguage.all())
                .extracting(SupportedLanguage::code)
                .containsExactly("en", "cy", "es", "fr", "de", "it", "pt", "pl", "nl", "zh", "ja", "ko", "ar", "hi");
    }

    @Test
    void normalisesLocaleTagsAndChineseVariants() {
        assertThat(SupportedLanguage.normalizeCode("fr-FR")).isEqualTo("fr");
        assertThat(SupportedLanguage.normalizeCode("pt_BR")).isEqualTo("pt");
        assertThat(SupportedLanguage.normalizeCode("zh-Hans-CN")).isEqualTo("zh");
    }

    @Test
    void fallsBackSafelyAndMarksArabicAsRightToLeft() {
        assertThat(SupportedLanguage.fromCode("unknown")).isEqualTo(SupportedLanguage.ENGLISH);
        assertThat(SupportedLanguage.ARABIC.rightToLeft()).isTrue();
        assertThat(SupportedLanguage.ENGLISH.rightToLeft()).isFalse();
    }
}
