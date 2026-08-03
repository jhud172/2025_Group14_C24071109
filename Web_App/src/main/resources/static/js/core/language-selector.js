(() => {
    'use strict';

    const normalise = (value) => (value || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLocaleLowerCase(document.documentElement.lang || 'en')
        .trim();

    document.querySelectorAll('[data-language-selector]').forEach((selector) => {
        const trigger = selector.querySelector('summary');
        const search = selector.querySelector('[data-language-search]');
        const options = Array.from(selector.querySelectorAll('[data-language-option]'));
        const empty = selector.querySelector('[data-language-empty]');
        const status = selector.querySelector('[data-language-search-status]');
        const searchBlock = selector.querySelector('[data-language-search-block]');
        const clearButton = selector.querySelector('[data-language-search-clear]');
        const availableLabel = selector.dataset.languageAvailableLabel || '';

        if (!trigger || !search || options.length === 0) {
            return;
        }

        const visibleOptions = () => options.filter((option) => !option.hidden);

        const announce = (count) => {
            if (!status) {
                return;
            }
            status.textContent = `${count} ${availableLabel}`.trim();
        };

        const filter = () => {
            const query = normalise(search.value);
            const hasQuery = query.length > 0;
            let matches = 0;

            options.forEach((option) => {
                const haystack = normalise(option.dataset.languageSearchValue);
                const visible = !hasQuery || haystack.includes(query);
                option.hidden = !visible;
                if (visible) {
                    matches += 1;
                }
            });

            if (empty) {
                empty.hidden = matches !== 0;
            }
            if (searchBlock) {
                searchBlock.classList.toggle('has-query', hasQuery);
            }
            if (clearButton) {
                clearButton.hidden = !hasQuery;
            }
            announce(matches);
        };

        const clearSearch = () => {
            search.value = '';
            filter();
            search.focus({preventScroll: true});
        };

        const moveFocus = (direction) => {
            const visible = visibleOptions();
            if (visible.length === 0) {
                search.focus();
                return;
            }
            const currentIndex = visible.indexOf(document.activeElement);
            const nextIndex = currentIndex < 0
                ? (direction > 0 ? 0 : visible.length - 1)
                : (currentIndex + direction + visible.length) % visible.length;
            visible[nextIndex].focus();
        };

        search.addEventListener('input', filter);
        search.addEventListener('keydown', (event) => {
            if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                event.preventDefault();
                moveFocus(event.key === 'ArrowDown' ? 1 : -1);
            }
        });

        if (clearButton) {
            clearButton.addEventListener('click', clearSearch);
        }

        options.forEach((option) => {
            option.addEventListener('keydown', (event) => {
                if (event.key === 'ArrowDown' || event.key === 'ArrowUp') {
                    event.preventDefault();
                    moveFocus(event.key === 'ArrowDown' ? 1 : -1);
                } else if (event.key.length === 1 && !event.ctrlKey && !event.metaKey && !event.altKey) {
                    search.focus();
                    search.value += event.key;
                    filter();
                }
            });
        });

        selector.addEventListener('toggle', () => {
            if (selector.open) {
                filter();
                window.requestAnimationFrame(() => search.focus({preventScroll: true}));
            } else {
                search.value = '';
                filter();
            }
        });

        selector.addEventListener('keydown', (event) => {
            if (event.key === 'Escape' && selector.open) {
                event.preventDefault();
                if (search.value.trim()) {
                    clearSearch();
                    return;
                }
                selector.open = false;
                trigger.focus();
            }
        });

        document.addEventListener('pointerdown', (event) => {
            if (selector.open && !selector.contains(event.target)) {
                selector.open = false;
            }
        });

        filter();
    });
})();
