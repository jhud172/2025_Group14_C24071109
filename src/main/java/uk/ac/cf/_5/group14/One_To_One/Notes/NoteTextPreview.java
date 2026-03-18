package uk.ac.cf._5.group14.One_To_One.Notes;

import org.jsoup.Jsoup;

public final class NoteTextPreview {
    private NoteTextPreview() {
    }

    public static String build(String html) {
        if (html == null || html.isBlank()) {
            return "";
        }
        String text = Jsoup.parse(html).text();
        if (text.length() > 140) {
            return text.substring(0, 137) + "...";
        }
        return text;
    }
}
