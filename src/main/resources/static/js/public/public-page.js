document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('opening-overlay');
    
    // INTRO SEQUENCE
    if (overlay) {
        // Total cinematic duration: 1400ms
        setTimeout(() => {
            overlay.classList.add('finished');
            
            // Garbage Collection: Remove from DOM after fade-out completes
            setTimeout(() => {
                if(overlay.parentNode) {
                    overlay.parentNode.removeChild(overlay);
                }
            }, 800); // Matches CSS transition time
        }, 1400); 
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
