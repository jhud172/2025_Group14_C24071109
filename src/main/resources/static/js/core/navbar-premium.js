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

    const overflowNav = document.querySelector('[data-overflow-nav]');
    if (overflowNav) {
        const track = overflowNav.querySelector('[data-overflow-track]');
        const moreWrapper = overflowNav.querySelector('[data-more-wrapper]');
        const moreToggle = overflowNav.querySelector('[data-more-toggle]');
        const moreMenu = overflowNav.querySelector('[data-more-menu]');
        const items = Array.from(overflowNav.querySelectorAll('[data-nav-item]'));

        if (track && moreWrapper && moreToggle && moreMenu && items.length) {
            items.forEach((item) => item.classList.add('nav-overflow-item'));

            const sorted = items.slice().sort((a, b) => {
                const ap = Number(a.dataset.priority || '999');
                const bp = Number(b.dataset.priority || '999');
                return bp - ap;
            });

            function closeMoreMenu() {
                moreMenu.classList.add('hidden');
                moreToggle.setAttribute('aria-expanded', 'false');
            }

            function openMoreMenu() {
                if (!moreWrapper.classList.contains('hidden')) {
                    moreMenu.classList.remove('hidden');
                    moreToggle.setAttribute('aria-expanded', 'true');
                }
            }

            function rebuildOverflowMenu(hiddenItems) {
                moreMenu.innerHTML = '';
                hiddenItems.forEach((item) => {
                    const clone = item.cloneNode(true);
                    clone.classList.remove('is-collapsed', 'nav-overflow-item');
                    clone.removeAttribute('data-nav-item');
                    clone.removeAttribute('data-priority');
                    clone.classList.add('w-full', 'justify-start');
                    moreMenu.appendChild(clone);
                });
            }

            function collapseItem(item) {
                item.classList.add('is-collapsed');
                item.setAttribute('aria-hidden', 'true');
            }

            function expandItem(item) {
                item.classList.remove('is-collapsed');
                item.removeAttribute('aria-hidden');
            }

            function updateOverflow() {
                closeMoreMenu();

                items.forEach((item) => expandItem(item));
                moreWrapper.classList.add('hidden');
                moreMenu.innerHTML = '';

                const hiddenItems = [];
                moreWrapper.classList.remove('hidden');

                // Hide low-priority items until links fit in available track width.
                for (const item of sorted) {
                    if (track.scrollWidth <= track.clientWidth) break;
                    collapseItem(item);
                    hiddenItems.push(item);
                }

                if (hiddenItems.length === 0) {
                    moreWrapper.classList.add('hidden');
                    return;
                }

                rebuildOverflowMenu(hiddenItems.reverse());

                // If the More trigger itself causes overflow, hide one more low-priority item.
                for (const item of sorted) {
                    if (track.scrollWidth <= track.clientWidth) break;
                    if (item.classList.contains('is-collapsed')) continue;
                    collapseItem(item);
                    hiddenItems.push(item);
                    rebuildOverflowMenu(hiddenItems.slice().reverse());
                }
            }

            let resizeRaf = null;
            function onResize() {
                if (resizeRaf !== null) {
                    cancelAnimationFrame(resizeRaf);
                }
                resizeRaf = requestAnimationFrame(() => {
                    updateOverflow();
                    resizeRaf = null;
                });
            }

            moreToggle.addEventListener('click', (event) => {
                event.preventDefault();
                event.stopPropagation();
                const expanded = moreToggle.getAttribute('aria-expanded') === 'true';
                if (expanded) {
                    closeMoreMenu();
                } else {
                    openMoreMenu();
                }
            });

            document.addEventListener('click', (event) => {
                if (!overflowNav.contains(event.target)) {
                    closeMoreMenu();
                }
            });

            document.addEventListener('keydown', (event) => {
                if (event.key === 'Escape') {
                    closeMoreMenu();
                }
            });

            window.addEventListener('resize', onResize);
            updateOverflow();
        }
    }
});
