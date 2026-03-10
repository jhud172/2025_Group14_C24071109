(function() {
    'use strict';
    var searchInput  = document.getElementById('product-search');
    var grid         = document.getElementById('product-grid');
    var noResults    = document.getElementById('no-results');
    var countNum     = document.getElementById('count-num');
    var clearBtn     = document.getElementById('clear-search');
    var catButtons   = document.querySelectorAll('#category-filters .cat-pill');

    var activeCategory = 'all';

    function applyFilters() {
        if (!grid) return;
        var query = searchInput ? searchInput.value.toLowerCase().trim() : '';
        var cards = grid.querySelectorAll('.product-card');
        var visible = 0;

        cards.forEach(function(card) {
            var text    = (card.getAttribute('data-search') || '').toLowerCase();
            var cat     = (card.getAttribute('data-cat') || '').toLowerCase();
            var inStock = card.getAttribute('data-in-stock') === 'true';

            var matchSearch = !query || text.includes(query);
            var matchCat    = activeCategory === 'all'
                           || (activeCategory === 'in-stock' ? inStock : cat === activeCategory.toLowerCase());

            var show = matchSearch && matchCat;
            card.style.display = show ? '' : 'none';
            if (show) visible++;
        });

        if (countNum) countNum.textContent = visible;
        if (noResults) noResults.classList.toggle('hidden', visible > 0 || (!query && activeCategory === 'all'));
    }

    if (searchInput) {
        searchInput.addEventListener('input', applyFilters);
    }

    catButtons.forEach(function(btn) {
        btn.addEventListener('click', function() {
            catButtons.forEach(function(b) { b.classList.remove('active'); });
            btn.classList.add('active');
            activeCategory = btn.getAttribute('data-cat');
            applyFilters();
        });
    });

    if (clearBtn) {
        clearBtn.addEventListener('click', function() {
            if (searchInput) searchInput.value = '';
            activeCategory = 'all';
            catButtons.forEach(function(b) { b.classList.remove('active'); });
            var allBtn = document.querySelector('.cat-pill.all');
            if (allBtn) allBtn.classList.add('active');
            applyFilters();
        });
    }
}());
