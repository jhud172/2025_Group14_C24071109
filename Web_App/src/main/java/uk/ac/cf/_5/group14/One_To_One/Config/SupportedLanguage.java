package uk.ac.cf._5.group14.One_To_One.Config;

import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

/**
 * The single source of truth for every language offered by the web app.
 *
 * <p>Keep the stable storage code short (for example {@code zh}) while using
 * the full locale tag ({@code zh-CN}) for locale-aware date and number
 * formatting.</p>
 */
public enum SupportedLanguage {
    ENGLISH("en", "en-GB", "EN", "English", "preferences.language.en", false),
    WELSH("cy", "cy-GB", "CY", "Cymraeg", "preferences.language.cy", false),
    SPANISH("es", "es-ES", "ES", "Español", "preferences.language.es", false),
    FRENCH("fr", "fr-FR", "FR", "Français", "preferences.language.fr", false),
    GERMAN("de", "de-DE", "DE", "Deutsch", "preferences.language.de", false),
    ITALIAN("it", "it-IT", "IT", "Italiano", "preferences.language.it", false),
    PORTUGUESE("pt", "pt-PT", "PT", "Português", "preferences.language.pt", false),
    POLISH("pl", "pl-PL", "PL", "Polski", "preferences.language.pl", false),
    DUTCH("nl", "nl-NL", "NL", "Nederlands", "preferences.language.nl", false),
    CHINESE_SIMPLIFIED("zh", "zh-CN", "ZH", "简体中文", "preferences.language.zh", false),
    JAPANESE("ja", "ja-JP", "JA", "日本語", "preferences.language.ja", false),
    KOREAN("ko", "ko-KR", "KO", "한국어", "preferences.language.ko", false),
    ARABIC("ar", "ar", "AR", "العربية", "preferences.language.ar", true),
    HINDI("hi", "hi-IN", "HI", "हिन्दी", "preferences.language.hi", false);

    public static final String DEFAULT_CODE = "en";
    public static final String SUPPORTED_LANGUAGE_PATTERN =
            "^(en|cy|es|fr|de|it|pt|pl|nl|zh|ja|ko|ar|hi)$";

    private static final List<SupportedLanguage> VALUES = List.of(values());
    private static final Map<String, SupportedLanguage> BY_CODE = Arrays.stream(values())
            .collect(Collectors.toUnmodifiableMap(SupportedLanguage::code, Function.identity()));

    private final String code;
    private final String localeTag;
    private final String shortLabel;
    private final String nativeName;
    private final String messageKey;
    private final boolean rightToLeft;

    SupportedLanguage(String code,
                      String localeTag,
                      String shortLabel,
                      String nativeName,
                      String messageKey,
                      boolean rightToLeft) {
        this.code = code;
        this.localeTag = localeTag;
        this.shortLabel = shortLabel;
        this.nativeName = nativeName;
        this.messageKey = messageKey;
        this.rightToLeft = rightToLeft;
    }

    public String code() {
        return code;
    }

    public String localeTag() {
        return localeTag;
    }

    public String shortLabel() {
        return shortLabel;
    }

    public String nativeName() {
        return nativeName;
    }

    public String messageKey() {
        return messageKey;
    }

    public boolean rightToLeft() {
        return rightToLeft;
    }

    public Locale locale() {
        return Locale.forLanguageTag(localeTag);
    }

    public static List<SupportedLanguage> all() {
        return VALUES;
    }

    public static SupportedLanguage fromCode(String candidate) {
        if (candidate == null || candidate.isBlank()) {
            return ENGLISH;
        }
        String normalized = candidate.trim().toLowerCase(Locale.ROOT).replace('_', '-');
        if (normalized.startsWith("zh")) {
            normalized = "zh";
        } else if (normalized.length() > 2 && normalized.charAt(2) == '-') {
            normalized = normalized.substring(0, 2);
        }
        return BY_CODE.getOrDefault(normalized, ENGLISH);
    }

    public static String normalizeCode(String candidate) {
        return fromCode(candidate).code();
    }
}
