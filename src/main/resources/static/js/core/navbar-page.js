(function () {
    const button = document.getElementById('siteNavButton');
    const menu = document.getElementById('siteNavMenu');
    if (!button || !menu) return;

    const mobileNavBreakpoint = window.matchMedia('(max-width: 1248px)');
    const header = button.closest('.navheader');

    const setOpen = (open) => {
        const nextOpen = mobileNavBreakpoint.matches && open;
        menu.classList.toggle('is-open', nextOpen);
        menu.setAttribute('aria-hidden', nextOpen ? 'false' : 'true');
        button.setAttribute('aria-expanded', nextOpen ? 'true' : 'false');

        if (header) {
            header.classList.toggle('navheader--menu-open', nextOpen);
        }
    };

    const isOpen = () => button.getAttribute('aria-expanded') === 'true';
    const closeMenu = () => setOpen(false);

    setOpen(false);

    button.addEventListener('click', (event) => {
        event.preventDefault();

        if (!mobileNavBreakpoint.matches) {
            closeMenu();
            return;
        }

        setOpen(!isOpen());
    });

    menu.addEventListener('click', (event) => {
        const actionable = event.target.closest('a, button');
        if (!actionable) return;
        closeMenu();
    });

    document.addEventListener('click', (event) => {
        if (!isOpen()) return;

        if (!menu.contains(event.target) && !button.contains(event.target)) {
            closeMenu();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && isOpen()) {
            closeMenu();
            button.focus();
        }
    });

    const syncLayout = () => {
        if (!mobileNavBreakpoint.matches) {
            closeMenu();
        }
    };

    if (typeof mobileNavBreakpoint.addEventListener === 'function') {
        mobileNavBreakpoint.addEventListener('change', syncLayout);
    } else if (typeof mobileNavBreakpoint.addListener === 'function') {
        mobileNavBreakpoint.addListener(syncLayout);
    }

    window.addEventListener('resize', syncLayout);

    const mobileQuickActionsToggle = document.getElementById('mobileQuickActionsToggle');
    if (mobileQuickActionsToggle) {
        mobileQuickActionsToggle.addEventListener('click', () => {
            const quickActionsToggle = document.getElementById('quickActionsToggle');
            if (quickActionsToggle) {
                quickActionsToggle.click();
                closeMenu();
            }
        });
    }

})();
