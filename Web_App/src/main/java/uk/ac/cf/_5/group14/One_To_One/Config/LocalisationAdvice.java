package uk.ac.cf._5.group14.One_To_One.Config;

import org.springframework.context.MessageSource;
import org.springframework.context.annotation.Bean;
import org.springframework.context.support.ResourceBundleMessageSource;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

import java.util.List;
import java.util.Locale;

@ControllerAdvice
public class LocalisationAdvice {

    @Bean(name = "messageSource")
    public MessageSource messageSource() {
        ResourceBundleMessageSource messageSource = new ResourceBundleMessageSource();
        messageSource.setBasenames("messages", "messages-home", "messages-ui");
        messageSource.setDefaultEncoding("UTF-8");
        messageSource.setFallbackToSystemLocale(false);
        return messageSource;
    }

    @ModelAttribute("supportedLanguages")
    public List<SupportedLanguage> supportedLanguages() {
        return SupportedLanguage.all();
    }

    @ModelAttribute("currentLanguage")
    public SupportedLanguage currentLanguage(Locale locale) {
        return SupportedLanguage.fromCode(locale == null ? null : locale.toLanguageTag());
    }

    @ModelAttribute("textDirection")
    public String textDirection(Locale locale) {
        return currentLanguage(locale).rightToLeft() ? "rtl" : "ltr";
    }
}
