package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import java.util.List;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public final class PaymentKeywordDetector {

    private static final List<Pattern> PATTERNS = List.of(
            Pattern.compile("\\bpaypal\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("paypal\\.me", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bvenmo\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("venmo\\.com", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bcashapp\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bcashtag\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bzelle\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\brevolut\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bwise\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bbacs\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bfaster\\s+payments\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bwire\\s+transfer\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bbank\\s+transfer\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bsort\\s+code\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\baccount\\s+number\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\biban\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bswift\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\brouting\\s+number\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bpay\\s+me\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bpay\\s+via\\b", Pattern.CASE_INSENSITIVE),
            Pattern.compile("\\bsend\\s+money\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bmeet\\s+and\\s+pay\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bcash\\b", Pattern.CASE_INSENSITIVE),
        Pattern.compile("\\bpay\\s+on\\s+meet\\b", Pattern.CASE_INSENSITIVE)
    );

    private static final List<Pattern> OBFUSCATION_PATTERNS = List.of(
        Pattern.compile("p\\s*a\\s*y\\s*p\\s*a\\s*l", Pattern.CASE_INSENSITIVE),
        Pattern.compile("v\\s*e\\s*n\\s*m\\s*o", Pattern.CASE_INSENSITIVE),
        Pattern.compile("c\\s*a\\s*s\\s*h\\s*a\\s*p\\s*p", Pattern.CASE_INSENSITIVE),
        Pattern.compile("r\\s*e\\s*v\\s*o\\s*l\\s*u\\s*t", Pattern.CASE_INSENSITIVE),
        Pattern.compile("w\\s*i\\s*s\\s*e", Pattern.CASE_INSENSITIVE)
    );

    private static final Pattern HANDLE_WITH_PAYMENT = Pattern.compile(
        "(pay|send|transfer|paypal|cashapp|venmo|revolut|wise)\\s*(to)?\\s*@\\w{2,}",
        Pattern.CASE_INSENSITIVE
    );

    private static final Pattern SORT_CODE_DIGITS = Pattern.compile("\\b(?:\\d[\\s-]*){6}\\b");
    private static final Pattern ACCOUNT_NUMBER_DIGITS = Pattern.compile("\\b(?:\\d[\\s-]*){8}\\b");
    private static final Pattern IBAN_PATTERN = Pattern.compile("\\b[A-Z]{2}\\d{2}[A-Z0-9]{10,30}\\b");

    private PaymentKeywordDetector() {
    }

    public static boolean containsOffPlatformPayment(String text) {
        return firstMatch(text) != null;
    }

    public static String firstMatch(String text) {
        if (text == null || text.isBlank()) {
            return null;
        }
        String normalized = text.toLowerCase(Locale.ROOT);
        for (Pattern pattern : PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                return matcher.group();
            }
        }

        for (Pattern pattern : OBFUSCATION_PATTERNS) {
            Matcher matcher = pattern.matcher(normalized);
            if (matcher.find()) {
                return matcher.group().replaceAll("\\s+", "");
            }
        }

        Matcher handleMatcher = HANDLE_WITH_PAYMENT.matcher(normalized);
        if (handleMatcher.find()) {
            return handleMatcher.group();
        }

        String collapsed = collapse(normalized);
        if (collapsed.contains("paypal")) {
            return "paypal";
        }
        if (collapsed.contains("venmo")) {
            return "venmo";
        }
        if (collapsed.contains("cashapp") || collapsed.contains("cashappme")) {
            return "cashapp";
        }
        if (collapsed.contains("revolut")) {
            return "revolut";
        }
        if (collapsed.contains("wise")) {
            return "wise";
        }
        if (collapsed.contains("banktransfer") || collapsed.contains("wiretransfer")) {
            return "bank transfer";
        }
        if (collapsed.contains("sortcode")) {
            return "sort code";
        }
        if (collapsed.contains("accountnumber")) {
            return "account number";
        }
        if (collapsed.contains("iban")) {
            return "iban";
        }
        if (collapsed.contains("swift")) {
            return "swift";
        }
        if (collapsed.contains("meetandpay")) {
            return "meet and pay";
        }

        if (SORT_CODE_DIGITS.matcher(normalized).find()) {
            return "sort code";
        }
        if (ACCOUNT_NUMBER_DIGITS.matcher(normalized).find()) {
            return "account number";
        }
        if (IBAN_PATTERN.matcher(normalized.replaceAll("\\s+", "").toUpperCase(Locale.ROOT)).find()) {
            return "iban";
        }
        return null;
    }

    private static String collapse(String value) {
        if (value == null) {
            return "";
        }
        return value.replaceAll("[^a-z0-9]", "");
    }
}
