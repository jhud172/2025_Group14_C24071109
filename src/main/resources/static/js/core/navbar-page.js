(function () {
    const button = document.getElementById('siteNavButton');
    const menu = document.getElementById('siteNavMenu');
    if (!button || !menu) return;

    const setOpen = (open) => {
        menu.classList.toggle('hidden', !open);
        button.setAttribute('aria-expanded', open ? 'true' : 'false');
    };

    const isOpen = () => button.getAttribute('aria-expanded') === 'true';

    setOpen(false);

    button.addEventListener('click', () => setOpen(!isOpen()));

    const mobileNav = menu.querySelector('[data-mobile-nav]');
    if (mobileNav) {
        mobileNav.addEventListener('click', (e) => {
            if (e.target.closest('a')) setOpen(false);
        });
    }

    document.addEventListener('keydown', (e) => {
        if (e.key === 'Escape' && isOpen()) setOpen(false);
    });
    
    // Mobile Quick Actions toggle
    const mobileQuickActionsToggle = document.getElementById('mobileQuickActionsToggle');
    if (mobileQuickActionsToggle) {
        mobileQuickActionsToggle.addEventListener('click', () => {
            const quickActionsToggle = document.getElementById('quickActionsToggle');
            if (quickActionsToggle) {
                quickActionsToggle.click();
                setOpen(false); // Close mobile menu
            }
        });
    }
    
    // Mobile Chat toggle
    const mobileChatToggle = document.getElementById('mobileChatToggle');
    if (mobileChatToggle) {
        mobileChatToggle.addEventListener('click', () => {
            const chatFab = document.getElementById('chatFab');
            if (chatFab) {
                chatFab.click();
                setOpen(false); // Close mobile menu
            }
        });
    }
})();
