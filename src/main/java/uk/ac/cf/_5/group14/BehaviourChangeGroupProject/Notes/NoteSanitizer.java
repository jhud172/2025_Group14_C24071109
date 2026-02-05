package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Notes;

import org.jsoup.Jsoup;
import org.jsoup.safety.Safelist;
import org.springframework.stereotype.Component;

@Component
public class NoteSanitizer {

    private final Safelist safelist = Safelist.relaxed()
            .addTags("span")
            .addAttributes("a", "target", "rel")
            .addProtocols("a", "href", "http", "https", "mailto")
            .preserveRelativeLinks(true);

    public String sanitize(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        return Jsoup.clean(html, safelist);
    }
}
