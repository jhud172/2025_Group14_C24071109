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

    let observer;
    let revealCheckQueued = false;

    const stopRevealChecksWhenComplete = () => {
        if (targets.some((target) => !target.classList.contains('is-visible'))) return;
        window.removeEventListener('scroll', queueRevealCheck);
        window.removeEventListener('resize', queueRevealCheck);
        document.removeEventListener('focusin', revealFocusedSection);
    };

    const revealReachedSections = () => {
        revealCheckQueued = false;
        targets.forEach((target) => {
            if (target.classList.contains('is-visible')) return;
            if (target.getBoundingClientRect().top > window.innerHeight * 0.92) return;
            reveal(target);
            observer.unobserve(target);
        });
        stopRevealChecksWhenComplete();
    };

    function queueRevealCheck() {
        if (revealCheckQueued) return;
        revealCheckQueued = true;
        window.requestAnimationFrame(revealReachedSections);
    }

    function revealFocusedSection(event) {
        const section = event.target.closest('[data-public-section], [data-public-hero]');
        if (!section || section.classList.contains('is-visible')) return;
        reveal(section);
        observer.unobserve(section);
        section.scrollIntoView({ block: 'nearest', behavior: 'auto' });
        stopRevealChecksWhenComplete();
    }

    observer = new IntersectionObserver((entries) => {
        entries.forEach((entry) => {
            if (!entry.isIntersecting) return;
            reveal(entry.target);
            observer.unobserve(entry.target);
        });
        stopRevealChecksWhenComplete();
    }, {
        rootMargin: '0px 0px -10% 0px',
        threshold: 0.08
    });

    targets.forEach((target) => observer.observe(target));
    window.addEventListener('scroll', queueRevealCheck, { passive: true });
    window.addEventListener('resize', queueRevealCheck);
    document.addEventListener('focusin', revealFocusedSection);
    revealReachedSections();
}());
