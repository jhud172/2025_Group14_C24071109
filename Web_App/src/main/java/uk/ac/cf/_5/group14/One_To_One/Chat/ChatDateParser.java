package uk.ac.cf._5.group14.One_To_One.Chat;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.Locale;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class ChatDateParser {

    private static final Pattern ISO = Pattern.compile("\\b(\\d{4}-\\d{2}-\\d{2})\\b");
    private static final Pattern DAY_MONTH = Pattern.compile("\\b(\\d{1,2})\\s+(jan|january|feb|february|mar|march|apr|april|may|jun|june|jul|july|aug|august|sep|sept|september|oct|october|nov|november|dec|december)\\b", Pattern.CASE_INSENSITIVE);

    private ChatDateParser() {}

    public static Optional<LocalDate> tryParse(String message) {
        if (message == null) return Optional.empty();

        Matcher iso = ISO.matcher(message);
        if (iso.find()) {
            try {
                return Optional.of(LocalDate.parse(iso.group(1)));
            } catch (DateTimeParseException ignored) {
            }
        }

        Matcher dm = DAY_MONTH.matcher(message);
        if (dm.find()) {
            String d = dm.group(1);
            String m = dm.group(2).toLowerCase(Locale.ROOT);
            String mon = m.length() >= 3 ? m.substring(0, 3) : m;

            int year = LocalDate.now().getYear();
            String composed = d + " " + mon + " " + year;
            DateTimeFormatter fmt = DateTimeFormatter.ofPattern("d MMM uuuu", Locale.ENGLISH);
            try {
                return Optional.of(LocalDate.parse(composed, fmt));
            } catch (DateTimeParseException ignored) {
            }
        }

        return Optional.empty();
    }
}
