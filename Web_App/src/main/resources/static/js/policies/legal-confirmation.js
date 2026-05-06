(function () {
    const box = document.getElementById('legal-confirm-box');
    if (!box) return;

    const key = box.dataset.legalConfirmKey;
    const pending = document.getElementById('legal-confirm-pending');
    const confirmed = document.getElementById('legal-confirmed-msg');
    const confirmButton = box.querySelector('[data-legal-confirm]');

    if (!key || !pending || !confirmed || !confirmButton) return;

    function setConfirmedState(isConfirmed) {
        pending.classList.toggle('hidden', isConfirmed);
        confirmed.classList.toggle('hidden', !isConfirmed);
        confirmed.classList.toggle('flex', isConfirmed);
    }

    if (localStorage.getItem('legal-confirmed-' + key) === '1') {
        setConfirmedState(true);
    }

    confirmButton.addEventListener('click', () => {
        localStorage.setItem('legal-confirmed-' + key, '1');
        setConfirmedState(true);
    });
}());
