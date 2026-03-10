const modal = document.getElementById('confirmModal');
const openModalBtn = document.getElementById('openConfirmModal');
const closeModalBtn = document.getElementById('closeConfirmModal');
const priceInput = document.getElementById('newPriceDollars');
const effectiveInput = document.getElementById('effectiveDate');
const confirmPrice = document.getElementById('confirmNewPrice');
const confirmDate = document.getElementById('confirmEffectiveDate');

function formatCurrency(value) {
    const number = Number(value || 0);
    return `$${number.toFixed(2)}`;
}

openModalBtn.addEventListener('click', () => {
    if (!priceInput.value || !document.getElementById('reason').value.trim() || !effectiveInput.value) {
        document.getElementById('priceChangeForm').reportValidity();
        return;
    }
    confirmPrice.textContent = formatCurrency(priceInput.value);
    confirmDate.textContent = effectiveInput.value ? effectiveInput.value : 'Next renewal';
    modal.classList.remove('hidden');
});

closeModalBtn.addEventListener('click', () => {
    modal.classList.add('hidden');
});

modal.addEventListener('click', (event) => {
    if (event.target === modal) {
        modal.classList.add('hidden');
    }
});
