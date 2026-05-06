// Workout search functionality
document.addEventListener('DOMContentLoaded', function() {
    const searchInput = document.getElementById('workoutSearch');
    if (!searchInput) return;

    searchInput.addEventListener('input', function(e) {
        const searchTerm = e.target.value.toLowerCase();
        const workoutCards = document.querySelectorAll('[data-title]');
        
        workoutCards.forEach(card => {
            const title = card.dataset.title || '';
            const matches = title.includes(searchTerm);
            
            card.style.display = matches ? '' : 'none';
        });
    });
});
