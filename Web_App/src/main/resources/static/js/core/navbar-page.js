(function () {
    const button = document.getElementById('siteNavButton');
    const menu = document.getElementById('siteNavMenu');
    if (!button || !menu) return;

    const mobileNavBreakpoint = window.matchMedia('(max-width: 880px)');
    const header = button.closest('.navheader');
    let lastScrollY = window.scrollY || 0;
    let ticking = false;

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

    const setHeaderVisible = (visible) => {
        if (!header) return;
        header.classList.toggle('navheader--hidden', !visible);
    };

    const syncScrollVisibility = () => {
        if (!header) return;
        const currentY = Math.max(window.scrollY || 0, 0);
        const delta = currentY - lastScrollY;
        const menuOpen = header.classList.contains('navheader--menu-open');
        const focused = header.contains(document.activeElement);

        if (currentY <= 20 || menuOpen || focused) {
            setHeaderVisible(true);
        } else if (Math.abs(delta) > 8) {
            setHeaderVisible(delta < 0);
        }

        header.classList.toggle('scrolled', currentY > 12);
        lastScrollY = currentY;
        ticking = false;
    };

    window.addEventListener('scroll', () => {
        if (!ticking) {
            window.requestAnimationFrame(syncScrollVisibility);
            ticking = true;
        }
    }, { passive: true });

    header?.addEventListener('focusin', () => setHeaderVisible(true));
    window.addEventListener('wheel', (event) => {
        if (event.deltaY < -2) setHeaderVisible(true);
    }, { passive: true });
    window.addEventListener('keydown', (event) => {
        if (['ArrowUp', 'PageUp', 'Home'].includes(event.key)) {
            setHeaderVisible(true);
        }
    });
    syncScrollVisibility();

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
