(function () {
    const audience = document.getElementById('outreach-audience');
    const wrap = document.getElementById('specific-email-wrap');
    const input = document.getElementById('specific-email');
    function sync() {
        if (!audience || !wrap || !input) return;
        const isSpecific = audience.value === 'SPECIFIC';
        wrap.classList.toggle('hidden', !isSpecific);
        input.required = isSpecific;
    }
    if (audience) {
        audience.addEventListener('change', sync);
        sync();
    }
}());
