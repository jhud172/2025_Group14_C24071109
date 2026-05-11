package uk.ac.cf._5.group14.BehaviourChangeGroupProject.Messaging;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;

class PaymentKeywordDetectorTest {

    @Test
    void detectsCommonPaymentPhrases() {
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("Please pay me via PayPal"))
                .isTrue();
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("My sort code is 12-34-56"))
                .isTrue();
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("Use venmo.com/handle"))
                .isTrue();
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("Send money through bank transfer"))
                .isTrue();
    }

    @Test
    void ignoresNormalConversation() {
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("Let's review your workout plan."))
                .isFalse();
        assertThat(PaymentKeywordDetector.containsOffPlatformPayment("We can pay attention to form."))
                .isFalse();
    }

    @Test
    void returnsFirstMatchKeyword() {
        String match = PaymentKeywordDetector.firstMatch("Pay me using PayPal please");
        assertThat(match).isNotNull();
        assertThat(match.toLowerCase()).contains("pay");
    }
}
