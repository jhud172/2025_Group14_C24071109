document.addEventListener('DOMContentLoaded', () => {
    const overlay = document.getElementById('opening-overlay');
    const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    const createDevModeNotificationController = () => {
        const notice = document.getElementById('devModeHomeNotice');
        if (!notice) {
            return null;
        }

        const card = notice.querySelector('[data-dev-mode-notification-card]');
        const closeButton = notice.querySelector('[data-dev-mode-notification-close]');
        const triggers = Array.from(document.querySelectorAll('[data-dev-mode-trigger]'));
        const autoDismissMs = Number.parseInt(notice.dataset.autoDismissMs || '5000', 10);
        let autoDismissTimer = null;
        let entranceTimer = null;
        let finishTimer = null;
        let badgeBounceTimer = null;

        const visibleTrigger = () => triggers.find((trigger) => {
            const rect = trigger.getBoundingClientRect();
            return rect.width > 0 && rect.height > 0;
        }) || triggers[0] || null;

        const clearTimers = () => {
            window.clearTimeout(autoDismissTimer);
            window.clearTimeout(entranceTimer);
            window.clearTimeout(finishTimer);
            window.clearTimeout(badgeBounceTimer);
        };

        const setTriggerState = (expanded) => {
            triggers.forEach((trigger) => {
                trigger.setAttribute('aria-controls', notice.id);
                trigger.setAttribute('aria-expanded', String(expanded));
            });
        };

        const bounceTrigger = (trigger, className, duration) => {
            if (!trigger || prefersReducedMotion) {
                return;
            }

            trigger.classList.remove('is-launching-notice', 'is-receiving-notice');
            void trigger.offsetWidth;
            trigger.classList.add(className);
            badgeBounceTimer = window.setTimeout(() => trigger.classList.remove(className), duration);
        };

        const setMorphTarget = () => {
            const trigger = visibleTrigger();
            if (!trigger || !card) {
                return trigger;
            }

            const cardRect = card.getBoundingClientRect();
            const triggerRect = trigger.getBoundingClientRect();
            const targetX = triggerRect.left + (triggerRect.width / 2) - (cardRect.left + (cardRect.width / 2));
            const targetY = triggerRect.top + (triggerRect.height / 2) - (cardRect.top + (cardRect.height / 2));
            const scaleX = Math.max(0.06, Math.min(1, triggerRect.width / cardRect.width));
            const scaleY = Math.max(0.12, Math.min(1, triggerRect.height / cardRect.height));

            notice.style.setProperty('--dev-notice-target-x', `${targetX}px`);
            notice.style.setProperty('--dev-notice-target-y', `${targetY}px`);
            notice.style.setProperty('--dev-notice-target-scale-x', scaleX.toFixed(3));
            notice.style.setProperty('--dev-notice-target-scale-y', scaleY.toFixed(3));
            return trigger;
        };

        const finishDismissal = () => {
            const trigger = visibleTrigger();
            notice.classList.remove('is-visible', 'is-entering', 'is-exiting');
            notice.hidden = true;
            notice.setAttribute('aria-hidden', 'true');
            notice.setAttribute('inert', '');
            setTriggerState(false);

        };

        const dismiss = () => {
            if (notice.hidden || notice.classList.contains('is-exiting')) {
                return;
            }

            clearTimers();
            const trigger = setMorphTarget();
            if (notice.contains(document.activeElement) && trigger) {
                trigger.focus({ preventScroll: true });
            }
            notice.setAttribute('aria-hidden', 'true');
            notice.setAttribute('inert', '');
            notice.classList.remove('is-entering');
            notice.classList.add('is-exiting');
            setTriggerState(false);

            if (prefersReducedMotion) {
                finishDismissal();
                return;
            }

            badgeBounceTimer = window.setTimeout(() => {
                bounceTrigger(trigger, 'is-receiving-notice', 720);
            }, 400);
            finishTimer = window.setTimeout(finishDismissal, 700);
        };

        const show = ({ trigger = null } = {}) => {
            clearTimers();
            notice.hidden = false;
            notice.removeAttribute('inert');
            notice.setAttribute('aria-hidden', 'false');
            notice.classList.remove('is-visible', 'is-entering', 'is-exiting');

            if (card) {
                void card.offsetWidth;
            }

            notice.classList.add('is-visible');
            if (!prefersReducedMotion) {
                notice.classList.add('is-entering');
                entranceTimer = window.setTimeout(() => notice.classList.remove('is-entering'), 780);
                bounceTrigger(trigger, 'is-launching-notice', 520);
            }

            setTriggerState(true);
            autoDismissTimer = window.setTimeout(dismiss, autoDismissMs);
        };

        closeButton?.addEventListener('click', dismiss);
        triggers.forEach((trigger) => {
            trigger.addEventListener('click', (event) => {
                event.preventDefault();
                show({ trigger });
            });
        });

        document.addEventListener('keydown', (event) => {
            if (event.key === 'Escape' && !notice.hidden) {
                dismiss();
            }
        });

        setTriggerState(false);
        return { show };
    };

    const devModeNotification = createDevModeNotificationController();
    const showDevModeNotification = () => {
        if (!devModeNotification) {
            return;
        }
        window.setTimeout(() => devModeNotification.show(), 120);
    };

    const finishOverlay = () => {
        if (!overlay || overlay.classList.contains('finished')) {
            return;
        }

        overlay.classList.add('finished');

        window.setTimeout(() => {
            if (overlay.parentNode) {
                overlay.parentNode.removeChild(overlay);
            }
            showDevModeNotification();
        }, 800);
    };

    // INTRO SEQUENCE
    if (overlay) {
        if (prefersReducedMotion) {
            overlay.remove();
            showDevModeNotification();
        } else {
            window.setTimeout(finishOverlay, 1400);
            overlay.addEventListener('animationend', (event) => {
                if (event.animationName === 'overlayDismiss') {
                    finishOverlay();
                }
            });
        }
    } else {
        showDevModeNotification();
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
