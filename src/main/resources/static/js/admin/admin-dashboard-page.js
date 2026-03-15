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

(function () {
    const forms = document.querySelectorAll('[data-dev-page-form]');
    forms.forEach((form) => {
        const select = form.querySelector('[data-dev-page-select]');
        const submit = form.querySelector('[data-dev-page-submit]');
        if (!select || !submit) {
            return;
        }

        const initial = select.dataset.initial || select.value;
        const sync = function () {
            const dirty = select.value !== initial;
            submit.disabled = !dirty;
        };

        select.addEventListener('change', sync);
        sync();
    });
}());
