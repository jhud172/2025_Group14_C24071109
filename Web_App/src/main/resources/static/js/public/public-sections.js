(function () {
    'use strict';

    const targets = Array.from(document.querySelectorAll('[data-public-section], [data-public-hero]'));
    if (!targets.length) return;

    const reducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
    document.documentElement.classList.add('public-motion-ready');

    const reveal = (element) => element.classList.add('is-visible');

    if (reducedMotion || !('IntersectionObserver' in window)) {
        targets.forEach(reveal);
        return;
    }

    const observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            reveal(entry.target);
            observer.unobserve(entry.target);
        });
    }, {
        rootMargin: '0px 0px -10% 0px',
        threshold: 0.08
    });

    targets.forEach((target) => {
        if (target.getBoundingClientRect().top < window.innerHeight * 0.92) {
            reveal(target);
        } else {
            observer.observe(target);
        }
    });
}());
