(() => {
    const searchInput = document.getElementById('faq-search');
    const items = Array.from(document.querySelectorAll('[data-faq-item]'));
    const empty = document.getElementById('faq-empty');
    const clearButton = document.querySelector('[data-faq-clear]');
    const resultsStatus = document.querySelector('[data-faq-results]');
    const resultsLabel = resultsStatus ? resultsStatus.dataset.faqResultsLabel : '';

    if (!searchInput || items.length === 0) {
        return;
    }

    const locale = document.documentElement.lang || 'en';
    const normalise = (value) => (value || '')
        .normalize('NFD')
        .replace(/[\u0300-\u036f]/g, '')
        .toLocaleLowerCase(locale)
        .trim();

    const updateResultsStatus = (shown) => {
        if (!resultsStatus) {
            return;
        }

        resultsStatus.textContent = shown === 0 && empty
            ? empty.textContent.trim()
            : [shown, items.length].join(' / ') + (resultsLabel ? ' · ' + resultsLabel : '');
    };

    const filterFaq = () => {
        const query = normalise(searchInput.value);
        let shown = 0;

        items.forEach((item) => {
            const match = query === '' || normalise(item.textContent).includes(query);
            item.hidden = !match;
            if (!match) {
                item.open = false;
            } else {
                shown += 1;
            }
        });

        if (empty) {
            empty.hidden = shown !== 0;
        }
        if (clearButton) {
            clearButton.hidden = query === '';
        }

        updateResultsStatus(shown);
    };

    const clearSearch = () => {
        searchInput.value = '';
        filterFaq();
        searchInput.focus();
    };

    searchInput.addEventListener('input', filterFaq);
    searchInput.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && searchInput.value !== '') {
            event.preventDefault();
            clearSearch();
        }
    });
    if (clearButton) {
        clearButton.addEventListener('click', clearSearch);
    }

    filterFaq();
})();
