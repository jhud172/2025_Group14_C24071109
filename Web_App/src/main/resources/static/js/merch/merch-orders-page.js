document.getElementById('order-search').addEventListener('input', function() {
    var query = this.value.toLowerCase();
    document.querySelectorAll('.order-card').forEach(function(card) {
        var text = (card.getAttribute('data-search') || '') +
                   card.textContent;
        card.style.display = text.toLowerCase().includes(query) ? '' : 'none';
    });
});
