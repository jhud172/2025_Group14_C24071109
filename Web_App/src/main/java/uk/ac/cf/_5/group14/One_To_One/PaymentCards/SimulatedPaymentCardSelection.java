package uk.ac.cf._5.group14.One_To_One.PaymentCards;

public record SimulatedPaymentCardSelection(
        SavedPaymentMethod savedPaymentMethod,
        String cardHolderName,
        String brand,
        String lastFour) {
}
