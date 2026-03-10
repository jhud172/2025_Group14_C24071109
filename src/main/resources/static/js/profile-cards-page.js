function openEditCard(btn) {
    var cardId = btn.getAttribute('data-card-id');
    var holder = btn.getAttribute('data-holder');
    var brand  = btn.getAttribute('data-brand');
    var month  = btn.getAttribute('data-month');
    var year   = btn.getAttribute('data-year');
    var isDef  = btn.getAttribute('data-default') === 'true';

    var modal = document.getElementById('edit-card-modal');
    var form  = document.getElementById('edit-card-form');
    form.action = '/profile/settings/cards/' + cardId + '/edit';
    document.getElementById('editCardHolder').value = holder;
    document.getElementById('editCardBrand').value  = brand;
    document.getElementById('editCardMonth').value  = month;
    document.getElementById('editCardYear').value   = year;
    document.getElementById('editCardDefault').checked = isDef;
    modal.classList.remove('hidden');
}
function closeEditCard() {
    document.getElementById('edit-card-modal').classList.add('hidden');
}
document.getElementById('edit-card-modal').addEventListener('click', function(e) {
    if (e.target === this) closeEditCard();
});

// Add-card form: extract last4 + generate stub provider token before submit.
// In production this would be replaced with a real payment-provider JS SDK.
var addCardForm = document.getElementById('add-card-form-settings');
if (addCardForm) {
    addCardForm.addEventListener('submit', function(e) {
        var displayInput = document.getElementById('addCardNumberDisplaySettings');
        var cleaned = displayInput.value.replace(/\s+/g, '');
        if (cleaned.length < 4) {
            e.preventDefault();
            alert('Please enter a valid card number.');
            return;
        }
        var last4 = cleaned.slice(-4);
        var token = 'tok_' + last4 + '_' + Date.now();
        document.getElementById('addLastFourSettings').value = last4;
        document.getElementById('addProviderTokenSettings').value = token;
        // Clear the raw number so it is not sent to the server
        displayInput.value = '';
        displayInput.removeAttribute('required');
    });
}
