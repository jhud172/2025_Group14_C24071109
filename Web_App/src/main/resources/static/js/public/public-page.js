document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('opening-overlay');

    const finishOverlay = () => {
        if (!overlay || overlay.classList.contains('finished')) {
            return;
        }

        overlay.classList.add('finished');

        window.setTimeout(() => {
            if (overlay.parentNode) {
                overlay.parentNode.removeChild(overlay);
            }
        }, 800);
    };

    // INTRO SEQUENCE
    if (overlay) {
        window.setTimeout(finishOverlay, 1400);
        overlay.addEventListener('animationend', (event) => {
            if (event.animationName === 'overlayDismiss') {
                finishOverlay();
            }
        });
    }

    // SCROLL REVEAL
    const observer = new IntersectionObserver((entries) => {
        entries.forEach(entry => {
            if (entry.isIntersecting) {
                entry.target.classList.add('in-view');
                // Optional: unobserve after revealing
                // observer.unobserve(entry.target);
            }
        });
    }, { threshold: 0.1 });

    document.querySelectorAll('.reveal-up').forEach((el) => observer.observe(el));

    const feedbackType = document.getElementById('feedback-type');
    const allowReply = document.getElementById('allow-email-reply');
    const queryReplyHint = document.getElementById('query-reply-hint');

    function syncReplyHint() {
        if (!feedbackType || !allowReply || !queryReplyHint) return;
        const isQuery = feedbackType.value === 'QUERY';
        queryReplyHint.classList.toggle('hidden', !isQuery);
        if (isQuery) {
            allowReply.required = true;
        } else {
            allowReply.required = false;
        }
    }

    if (feedbackType) {
        feedbackType.addEventListener('change', syncReplyHint);
        syncReplyHint();
    }
});
