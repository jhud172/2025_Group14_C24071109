// Exercise search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('exerciseSearch');
    if (!searchInput) return;

    searchInput.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        const exerciseCards = document.querySelectorAll('.exercise-card');
        
        exerciseCards.forEach(card => {
            const name = card.dataset.name || '';
            const muscles = card.dataset.muscles || '';
            const matches = name.includes(searchTerm) || muscles.includes(searchTerm);
            
            card.style.display = matches ? '' : 'none';
        });
    });
});
