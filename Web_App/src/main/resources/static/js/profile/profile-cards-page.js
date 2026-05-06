function openEditCard(button) {
    if (!button) return;

    var cardId = button.getAttribute('data-card-id');
    var holder = button.getAttribute('data-holder');
    var brand  = button.getAttribute('data-brand');
    var month  = button.getAttribute('data-month');
    var year   = button.getAttribute('data-year');
    var isDef  = button.getAttribute('data-default') === 'true';

    var modal = document.getElementById('edit-card-modal');
    var form  = document.getElementById('edit-card-form');
    if (!modal || !form) return;

    form.action = '/profile/settings/cards/' + encodeURIComponent(cardId) + '/edit';
    document.getElementById('editCardHolder').value = holder;
    document.getElementById('editCardBrand').value  = brand;
    document.getElementById('editCardMonth').value  = month;
    document.getElementById('editCardYear').value   = year;
    document.getElementById('editCardDefault').checked = isDef;
    modal.classList.remove('hidden');
}

function closeEditCard() {
    var modal = document.getElementById('edit-card-modal');
    if (modal) {
        modal.classList.add('hidden');
    }
}

var editCardModal = document.getElementById('edit-card-modal');
if (editCardModal) {
    editCardModal.addEventListener('click', function (event) {
        if (event.target === editCardModal) closeEditCard();
    });
}

document.querySelectorAll('[data-open-edit-card]').forEach(function (button) {
    button.addEventListener('click', function () {
        openEditCard(button);
    });
});

document.querySelectorAll('[data-close-edit-card]').forEach(function (button) {
    button.addEventListener('click', closeEditCard);
});

document.querySelectorAll('form[data-confirm-submit]').forEach(function (form) {
    form.addEventListener('submit', function (event) {
        var message = form.getAttribute('data-confirm-submit') || 'Are you sure?';
        if (!window.confirm(message)) {
            event.preventDefault();
        }
    });
});

// Add-card form: extract last4 + generate stub provider token before submit.
// In production this would be replaced with a real payment-provider JS SDK.
var addCardForm = document.getElementById('add-card-form-settings');
var addCardNumberDisplay = document.getElementById('addCardNumberDisplaySettings');
if (addCardNumberDisplay) {
    addCardNumberDisplay.addEventListener('input', function () {
        addCardNumberDisplay.value = addCardNumberDisplay.value.replace(/[^\d\s]/g, '');
    });
}
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
