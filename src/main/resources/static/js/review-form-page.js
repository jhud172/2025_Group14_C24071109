let currentRating = 0;

function setRating(rating) {
    currentRating = rating;
    document.getElementById('starsInput').value = rating;
    
    // Update star colors
    const buttons = document.querySelectorAll('.star-btn');
    buttons.forEach((btn, index) => {
        const svg = btn.querySelector('svg');
        if (index < rating) {
            svg.classList.remove('text-slate-300');
            svg.classList.add('text-yellow-400');
        } else {
            svg.classList.remove('text-yellow-400');
            svg.classList.add('text-slate-300');
        }
    });

    // Update rating text
    const texts = ['', 'Poor', 'Fair', 'Good', 'Very Good', 'Excellent'];
    document.getElementById('ratingText').textContent = texts[rating];
}

// Collect tags on form submit
document.querySelector('form').addEventListener('submit', function(e) {
    const checkboxes = document.querySelectorAll('input[name="tag"]:checked');
    const tags = Array.from(checkboxes).map(cb => cb.value).join(',');
    document.getElementById('tagsInput').value = tags;
});
