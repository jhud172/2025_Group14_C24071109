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

    const createTabController = ({ tabSelector, panelSelector, keyName }) => {
        const tabs = Array.from(document.querySelectorAll(tabSelector));
        const panels = Array.from(document.querySelectorAll(panelSelector));
        if (!tabs.length || !panels.length) {
            return;
        }

        const activate = (tab, moveFocus = false) => {
            const value = tab.dataset[keyName];
            tabs.forEach((candidate) => {
                const isSelected = candidate === tab;
                candidate.classList.toggle('is-active', isSelected);
                candidate.setAttribute('aria-selected', String(isSelected));
                candidate.tabIndex = isSelected ? 0 : -1;
            });
            panels.forEach((panel) => {
                const isSelected = panel.dataset[keyName.replace('Tab', 'Panel')] === value;
                panel.classList.toggle('is-active', isSelected);
                panel.hidden = !isSelected;
            });
            if (moveFocus) {
                tab.focus();
            }
        };

        tabs.forEach((tab, index) => {
            tab.addEventListener('click', () => activate(tab));
            tab.addEventListener('keydown', (event) => {
                if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(event.key)) {
                    return;
                }
                event.preventDefault();
                let nextIndex = index;
                if (event.key === 'ArrowRight') nextIndex = (index + 1) % tabs.length;
                if (event.key === 'ArrowLeft') nextIndex = (index - 1 + tabs.length) % tabs.length;
                if (event.key === 'Home') nextIndex = 0;
                if (event.key === 'End') nextIndex = tabs.length - 1;
                activate(tabs[nextIndex], true);
            });
        });
    };

    createTabController({
        tabSelector: '[data-preview-tab]',
        panelSelector: '[data-preview-panel]',
        keyName: 'previewTab'
    });
    createTabController({
        tabSelector: '[data-workspace-tab]',
        panelSelector: '[data-workspace-panel]',
        keyName: 'workspaceTab'
    });

    const brandObject = document.querySelector('[data-brand-object]');
    if (brandObject && !prefersReducedMotion) {
        const setBrandPose = (x, y) => {
            brandObject.style.setProperty('--brand-rotate-x', `${(-4 - (y * 10)).toFixed(2)}deg`);
            brandObject.style.setProperty('--brand-rotate-y', `${(-9 + (x * 14)).toFixed(2)}deg`);
            brandObject.style.setProperty('--brand-shine-x', `${50 + (x * 28)}%`);
            brandObject.style.setProperty('--brand-shine-y', `${45 + (y * 25)}%`);
        };

        brandObject.addEventListener('pointermove', (event) => {
            const bounds = brandObject.getBoundingClientRect();
            setBrandPose(
                ((event.clientX - bounds.left) / bounds.width) - 0.5,
                ((event.clientY - bounds.top) / bounds.height) - 0.5
            );
        });
        brandObject.addEventListener('pointerleave', () => setBrandPose(0, 0));
        brandObject.addEventListener('blur', () => setBrandPose(0, 0));
        brandObject.addEventListener('keydown', (event) => {
            const poses = {
                ArrowLeft: [-0.45, 0],
                ArrowRight: [0.45, 0],
                ArrowUp: [0, -0.4],
                ArrowDown: [0, 0.4]
            };
            if (!poses[event.key]) return;
            event.preventDefault();
            setBrandPose(...poses[event.key]);
        });
    }

    const sessionControl = document.querySelector('[data-session-control]');
    sessionControl?.addEventListener('click', () => {
        const isRunning = sessionControl.getAttribute('aria-pressed') === 'true';
        sessionControl.setAttribute('aria-pressed', String(!isRunning));
        sessionControl.classList.toggle('is-running', !isRunning);
        const label = sessionControl.querySelector('[data-session-label]');
        if (label) {
            label.textContent = isRunning ? 'Continue session' : 'Session active';
        }
    });

    const standardItems = Array.from(document.querySelectorAll('[data-standard-item]'));
    if (standardItems.length && 'IntersectionObserver' in window) {
        const standardObserver = new IntersectionObserver((entries) => {
            entries.forEach((entry) => {
                if (!entry.isIntersecting) return;
                standardItems.forEach((item) => item.classList.toggle('is-current', item === entry.target));
            });
        }, { rootMargin: '-35% 0px -45% 0px', threshold: 0 });
        standardItems.forEach((item) => standardObserver.observe(item));
        standardItems[0].classList.add('is-current');
    } else {
        standardItems[0]?.classList.add('is-current');
    }

    const depthStage = document.querySelector('[data-home-depth]');
    if (depthStage && !prefersReducedMotion) {
        const updateDepth = () => {
            const bounds = depthStage.getBoundingClientRect();
            const viewportMiddle = window.innerHeight / 2;
            const offset = Math.max(-1, Math.min(1, (bounds.top + bounds.height / 2 - viewportMiddle) / window.innerHeight));
            depthStage.style.setProperty('--depth-y', `${(offset * -12).toFixed(1)}px`);
        };
        updateDepth();
        window.addEventListener('scroll', updateDepth, { passive: true });
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
