// Day view swipe navigation
document.addEventListener("DOMContentLoaded", () => {
    const dayContainer = document.querySelector('[data-testid="day-hub-header"]');
    if (!dayContainer) return;

    const mainContent = document.getElementById('day-main-content');
    const reduceMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    if (mainContent) {
        const enterDirection = sessionStorage.getItem('day-nav-enter-direction');
        if (enterDirection) {
            mainContent.classList.add(enterDirection === 'from-next' ? 'day-nav-enter-left' : 'day-nav-enter-right');
            sessionStorage.removeItem('day-nav-enter-direction');
            window.setTimeout(() => {
                mainContent.classList.remove('day-nav-enter-left', 'day-nav-enter-right');
            }, 260);
        }
    }

    let touchStartX = 0;
    let touchEndX = 0;
    let touchStartY = 0;
    let touchEndY = 0;

    const minSwipeDistance = 50; // Minimum distance for a swipe
    const maxVerticalDistance = 100; // Maximum vertical movement allowed

    /** Animate exit then navigate */
    function navigateWithTransition(href, direction) {
        if (!mainContent || reduceMotion) {
            if (direction) {
                sessionStorage.setItem('day-nav-enter-direction', direction === 'next' ? 'from-next' : 'from-prev');
            }
            window.location.href = href;
            return;
        }

        mainContent.classList.remove('day-nav-leave-left', 'day-nav-leave-right');
        mainContent.classList.add(direction === 'next' ? 'day-nav-leave-left' : 'day-nav-leave-right');
        sessionStorage.setItem('day-nav-enter-direction', direction === 'next' ? 'from-next' : 'from-prev');

        setTimeout(() => { window.location.href = href; }, 180);
    }

    function handleSwipe() {
        const horizontalDistance = touchEndX - touchStartX;
        const verticalDistance = Math.abs(touchEndY - touchStartY);

        // Only process horizontal swipes (prevent interference with vertical scrolling)
        if (verticalDistance > maxVerticalDistance) return;

        if (Math.abs(horizontalDistance) < minSwipeDistance) return;

        if (horizontalDistance > 0) {
            // Swiped right - go to previous day
            const prevLink = document.querySelector('a[aria-label="Previous day"]');
            if (prevLink) navigateWithTransition(prevLink.href, 'prev');
        } else {
            // Swiped left - go to next day
            const nextLink = document.querySelector('a[aria-label="Next day"]');
            if (nextLink) navigateWithTransition(nextLink.href, 'next');
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
            if (prevLink) navigateWithTransition(prevLink.href, 'prev');
        } else if (e.key === 'ArrowRight') {
            e.preventDefault();
            const nextLink = document.querySelector('a[aria-label="Next day"]');
            if (nextLink) navigateWithTransition(nextLink.href, 'next');
        }
    });

    // Smooth transition on nav link clicks (prev/next day buttons)
    document.querySelectorAll('a[aria-label="Previous day"], a[aria-label="Next day"]').forEach(link => {
        link.addEventListener('click', (e) => {
            e.preventDefault();
            const direction = link.getAttribute('aria-label') === 'Next day' ? 'next' : 'prev';
            navigateWithTransition(link.href, direction);
        });
    });
});
