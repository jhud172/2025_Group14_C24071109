// Show/hide new card fields when radios change
document.querySelectorAll('input[name="selectedCardId"]').forEach(function(radio) {
    radio.addEventListener('change', function() {
        var newCardFields = document.getElementById('new-card-fields');
        if (newCardFields) {
            newCardFields.classList.toggle('hidden', this.value !== '');
        }
    });
});

// Checkout form: extract last4 + generate stub provider token before submit.
// In production this would be replaced with a real payment-provider JS SDK.
var checkoutForm = document.querySelector('form[action*="/buy"]');
if (checkoutForm) {
    checkoutForm.addEventListener('submit', function(e) {
        var newCardFields = document.getElementById('new-card-fields');
        if (!newCardFields || newCardFields.classList.contains('hidden')) return;
        var displayInput = document.getElementById('checkoutCardNumberDisplay');
        if (!displayInput || !displayInput.value.trim()) {
            e.preventDefault();
            alert('Please enter your card number.');
            return;
        }
        var cleaned = displayInput.value.replace(/\s+/g, '');
        if (cleaned.length < 4) {
            e.preventDefault();
            alert('Please enter a valid card number.');
            return;
        }
        var last4 = cleaned.slice(-4);
        var token = 'tok_' + last4 + '_' + Date.now();
        document.getElementById('checkoutLastFour').value = last4;
        document.getElementById('checkoutProviderToken').value = token;
        displayInput.value = '';
    });
}
