// Day view swipe navigation
document.addEventListener("DOMContentLoaded", () => {
    const dayContainer = document.querySelector('[data-testid="day-hub-header"]');
    if (!dayContainer) return;

    let touchStartX = 0;
    let touchEndX = 0;
    let touchStartY = 0;
    let touchEndY = 0;

    const minSwipeDistance = 50; // Minimum distance for a swipe
    const maxVerticalDistance = 100; // Maximum vertical movement allowed

    function handleSwipe() {
        const horizontalDistance = touchEndX - touchStartX;
        const verticalDistance = Math.abs(touchEndY - touchStartY);

        // Only process horizontal swipes (prevent interference with vertical scrolling)
        if (verticalDistance > maxVerticalDistance) return;

        if (Math.abs(horizontalDistance) < minSwipeDistance) return;

        if (horizontalDistance > 0) {
            // Swiped right - go to previous day
            const prevLink = document.querySelector('a[aria-label="Previous day"]');
            if (prevLink) prevLink.click();
        } else {
            // Swiped left - go to next day
            const nextLink = document.querySelector('a[aria-label="Next day"]');
            if (nextLink) nextLink.click();
        }
    }

    // Touch events for mobile
    document.addEventListener('touchstart', (e) => {
        touchStartX = e.changedTouches[0].screenX;
        touchStartY = e.changedTouches[0].screenY;
    });

    document.addEventListener('touchend', (e) => {
        touchEndX = e.changedTouches[0].screenX;
        touchEndY = e.changedTouches[0].screenY;
        handleSwipe();
    });

    // Keyboard navigation
    document.addEventListener('keydown', (e) => {
        // Don't interfere with form inputs
        if (e.target.tagName === 'INPUT' || e.target.tagName === 'TEXTAREA' || e.target.tagName === 'SELECT') {
            return;
        }

        if (e.key === 'ArrowLeft') {
            e.preventDefault();
            const prevLink = document.querySelector('a[aria-label="Previous day"]');
            if (prevLink) prevLink.click();
        } else if (e.key === 'ArrowRight') {
            e.preventDefault();
            const nextLink = document.querySelector('a[aria-label="Next day"]');
            if (nextLink) nextLink.click();
        }
    });
});
