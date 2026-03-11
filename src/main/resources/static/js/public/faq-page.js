(function () {
    const searchInput = document.getElementById('faq-search');
    const items = Array.from(document.querySelectorAll('[data-faq-item]'));
    const empty = document.getElementById('faq-empty');

    function filterFaq() {
        const q = (searchInput && searchInput.value ? searchInput.value : '').trim().toLowerCase();
        let shown = 0;
        items.forEach((item) => {
            const text = (item.textContent + ' ' + (item.getAttribute('data-keywords') || '')).toLowerCase();
            const match = q === '' || text.includes(q);
            item.classList.toggle('faq-hidden', !match);
            if (match) shown += 1;
        });
        if (empty) empty.classList.toggle('faq-hidden', shown !== 0);
    }

    if (searchInput) searchInput.addEventListener('input', filterFaq);
}());
