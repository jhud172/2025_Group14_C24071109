document.addEventListener('DOMContentLoaded', () => {
    const groups = document.querySelectorAll('[data-premium-badge-group]');
    if (!groups.length) return;

    function setOpen(group, open) {
        const toggle = group.querySelector('[data-premium-toggle]');
        group.setAttribute('data-open', open ? 'true' : 'false');
        if (toggle) {
            toggle.setAttribute('aria-expanded', open ? 'true' : 'false');
        }
    }

    function closeAll() {
        groups.forEach((group) => setOpen(group, false));
    }

    groups.forEach((group) => {
        const toggle = group.querySelector('[data-premium-toggle]');
        if (!toggle) return;

        toggle.addEventListener('click', (event) => {
            event.preventDefault();
            event.stopPropagation();
            const isOpen = group.getAttribute('data-open') === 'true';
            closeAll();
            setOpen(group, !isOpen);
        });

        toggle.addEventListener('keydown', (event) => {
            if (event.key !== 'Escape') return;
            setOpen(group, false);
            toggle.blur();
        });
    });

    document.addEventListener('click', (event) => {
        const inside = event.target.closest('[data-premium-badge-group]');
        if (!inside) {
            closeAll();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeAll();
        }
    });
});
