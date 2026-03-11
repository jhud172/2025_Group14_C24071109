(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', function () {
    var prefersReducedMotion = window.matchMedia && window.matchMedia('(prefers-reduced-motion: reduce)').matches;

    // Optional intro splash
    var introSplash = document.getElementById('introSplash');
    if (introSplash) {
      if (prefersReducedMotion) {
        introSplash.remove();
      } else {
        window.setTimeout(function () {
          introSplash.classList.add('pointer-events-none');
          introSplash.classList.remove('opacity-100');
          introSplash.classList.add('opacity-0');
          window.setTimeout(function () { introSplash.remove(); }, 1200);
        }, 650);
      }
    }

    // Brand crash-in
    var brandCrash = document.getElementById('brandCrash');
    if (brandCrash) {
      if (prefersReducedMotion) {
        brandCrash.classList.remove('opacity-0', 'translate-y-3', 'scale-95', 'blur-sm');
        brandCrash.classList.add('opacity-100');
      } else {
        window.setTimeout(function () {
          brandCrash.classList.remove('opacity-0', 'translate-y-3', 'scale-95', 'blur-sm');
          brandCrash.classList.add('opacity-100', 'translate-y-0', 'scale-100', 'blur-0');
        }, 120);
      }
    }

    // Reveal-on-scroll (used by public landing)
    var revealEls = Array.prototype.slice.call(document.querySelectorAll('.js-reveal'));
    if (revealEls.length) {
      var revealNow = function (el) {
        el.classList.remove('opacity-0', 'translate-y-2', 'translate-y-3', 'blur-sm');
        el.classList.add('opacity-100', 'translate-y-0', 'blur-0');
      };

      if (prefersReducedMotion || !('IntersectionObserver' in window)) {
        revealEls.forEach(revealNow);
      } else {
        var observer = new IntersectionObserver(function (entries, obs) {
          entries.forEach(function (entry) {
            if (!entry.isIntersecting) return;
            var el = entry.target;
            var delay = el.dataset.revealDelay ? Number(el.dataset.revealDelay) : 0;
            el.style.transitionDelay = delay + 'ms';
            revealNow(el);
            obs.unobserve(el);
          });
        }, { threshold: 0.15, rootMargin: '0px 0px -10% 0px' });

        revealEls.forEach(function (el, idx) {
          el.dataset.revealDelay = String(Math.min(idx * 70, 420));
          observer.observe(el);
        });
      }
    }

    // Segmented tabs (feature detail)
    var tabButtons = Array.prototype.slice.call(document.querySelectorAll('[role="tab"][data-tab]'));
    var tabPanels = {
      calendar: document.getElementById('feat-panel-calendar'),
      workouts: document.getElementById('feat-panel-workouts'),
      marketplace: document.getElementById('feat-panel-marketplace'),
      analytics: document.getElementById('feat-panel-analytics'),
      ai: document.getElementById('feat-panel-ai')
    };

    var setActiveTab = function (key) {
      tabButtons.forEach(function (btn) {
        var isActive = btn.dataset.tab === key;
        btn.setAttribute('aria-selected', isActive ? 'true' : 'false');
      });
      Object.entries(tabPanels).forEach(function (entry) {
        var panelKey = entry[0];
        var panelEl = entry[1];
        if (!panelEl) return;
        if (panelKey === key) panelEl.classList.remove('hidden');
        else panelEl.classList.add('hidden');
      });
    };

    if (tabButtons.length) {
      tabButtons.forEach(function (btn) {
        btn.addEventListener('click', function () { setActiveTab(btn.dataset.tab); });
      });
    }

    // AI demo bubble
    var aiBubble = document.getElementById('aiDemoBubble');
    var aiButtons = Array.prototype.slice.call(document.querySelectorAll('.ai-demo-btn[data-ai]'));
    var aiCopy = {
      strength: 'Strength focus: 3 sessions/week, progressive overload, and one simple signal - reps at a fixed RPE. We\'ll keep it structured and sustainable.',
      fatloss: 'Fat loss focus: keep training consistent (2-3 sessions), pair it with simple habits, and track adherence first. Small weekly adjustments only.',
      busy: 'Busy week: we\'ll compress to two key sessions, keep intensity sensible, and set a "minimum effective" habit so momentum stays intact.'
    };

    if (aiBubble && aiButtons.length) {
      aiButtons.forEach(function (btn) {
        btn.addEventListener('click', function () {
          var key = btn.dataset.ai;
          aiBubble.textContent = aiCopy[key] || aiCopy.strength;
        });
      });
    }

    initProteinNudgeBanner();
    initDismissibleInsights();
    initStreakMilestoneAnimation();

    function initProteinNudgeBanner() {
      var banner = document.getElementById('proteinNudgeBanner');
      var title = document.getElementById('proteinNudgeTitle');
      var message = document.getElementById('proteinNudgeMessage');
      var cta = document.getElementById('proteinNudgeCta');
      var dismiss = document.getElementById('proteinNudgeDismiss');
      if (!banner || !title || !message || !cta || !dismiss) return;

      var csrfToken = document.getElementById('chat_csrf') && document.getElementById('chat_csrf').value || null;
      var csrfHeader = document.getElementById('chat_csrf_header') && document.getElementById('chat_csrf_header').value || 'X-CSRF-TOKEN';

      var toLocalKey = function (date) {
        var year = date.getFullYear();
        var month = String(date.getMonth() + 1).padStart(2, '0');
        var day = String(date.getDate()).padStart(2, '0');
        return year + '-' + month + '-' + day;
      };

      var todayKey = toLocalKey(new Date());

      var isSameDay = function (iso) {
        if (!iso) return false;
        var parsed = new Date(iso);
        if (Number.isNaN(parsed.getTime())) return false;
        return toLocalKey(parsed) === todayKey;
      };

      var markRead = async function (id) {
        if (!id) return;
        var headers = {};
        if (csrfToken) headers[csrfHeader] = csrfToken;
        await fetch('/api/notifications/' + id + '/read', { method: 'POST', headers: headers });
      };

      var showBanner = function (notification) {
        title.textContent = notification.title || 'Protein-first check-in';
        message.textContent = notification.message || 'Log your protein to support recovery.';
        cta.href = notification.ctaUrl || '/nutrition';
        banner.classList.remove('hidden');

        dismiss.addEventListener('click', async function () {
          await markRead(notification.id);
          banner.classList.add('hidden');
        });
      };

      var loadProteinNudge = async function () {
        try {
          var res = await fetch('/api/notifications?limit=20');
          if (!res.ok) return;
          var data = await res.json();
          if (!Array.isArray(data)) return;

          var notification = data.find(function (n) {
            return n.type === 'PROTEIN_NUDGE' && !n.readAt && !n.dismissedAt && isSameDay(n.createdAt);
          });

          if (notification) {
            showBanner(notification);
          }
        } catch (e) {
          // ignore
        }
      };

      loadProteinNudge();
    }

    function initStreakMilestoneAnimation() {
      var card = document.getElementById('weekly-progress-card');
      if (!card) return;

      var streakCount = parseInt(card.getAttribute('data-streak'), 10);
      if (Number.isNaN(streakCount)) streakCount = 0;

      var milestones = [3, 7, 14, 30];
      var prevStreak = 0;

      try {
        prevStreak = parseInt(localStorage.getItem('prevStreak') || '0', 10);
        if (Number.isNaN(prevStreak)) prevStreak = 0;
        localStorage.setItem('prevStreak', String(streakCount));
      } catch (e) {
        prevStreak = 0;
      }

      if (!(streakCount > prevStreak && milestones.includes(streakCount))) return;

      window.setTimeout(function () {
        card.classList.add('streak-milestone-glow');
        window.setTimeout(function () {
          card.classList.remove('streak-milestone-glow');
        }, 2000);

        if (streakCount >= 7) {
          createConfetti(card);
        }
      }, 500);
    }

    function createConfetti(container) {
      var colors = ['#00f5ff', '#00ff9f', '#ff00ff'];
      container.classList.add('streak-confetti-host');

      for (var i = 0; i < 12; i++) {
        var confetti = document.createElement('div');
        confetti.className = 'confetti-particle';
        confetti.style.setProperty('--confetti-color', colors[Math.floor(Math.random() * colors.length)]);
        confetti.style.setProperty('--confetti-delay', String(i * 0.05) + 's');
        confetti.style.setProperty('--confetti-rot', String(Math.floor(Math.random() * 360)) + 'deg');
        confetti.style.setProperty('--confetti-x', String(Math.floor((Math.random() * 160) - 80)) + 'px');
        confetti.style.setProperty('--confetti-y', String(-60 - Math.floor(Math.random() * 60)) + 'px');
        container.appendChild(confetti);

        window.setTimeout(function (particle) {
          if (particle && particle.parentNode) {
            particle.parentNode.removeChild(particle);
          }
        }, 2000, confetti);
      }
    }

    function initDismissibleInsights() {
      var STORAGE_KEY = 'dismissedInsights';
      var STORAGE_EXPIRY = 7 * 24 * 60 * 60 * 1000;

      var loadDismissed = function () {
        try {
          var stored = localStorage.getItem(STORAGE_KEY);
          if (!stored) return {};
          var data = JSON.parse(stored);
          var now = Date.now();
          var valid = {};

          Object.entries(data).forEach(function (entry) {
            var insightId = entry[0];
            var timestamp = entry[1];
            if (now - timestamp < STORAGE_EXPIRY) {
              valid[insightId] = timestamp;
            }
          });

          return valid;
        } catch (e) {
          return {};
        }
      };

      var saveDismissed = function (insightId) {
        try {
          var dismissed = loadDismissed();
          dismissed[insightId] = Date.now();
          localStorage.setItem(STORAGE_KEY, JSON.stringify(dismissed));
        } catch (e) {
          // Silently fail if localStorage is unavailable.
        }
      };

      var dismissed = loadDismissed();
      document.querySelectorAll('[data-dismissible]').forEach(function (card) {
        var insightId = card.getAttribute('data-dismissible');
        if (dismissed[insightId]) {
          card.style.display = 'none';
        }
      });

      document.querySelectorAll('.dismiss-insight').forEach(function (btn) {
        btn.addEventListener('click', function (e) {
          e.preventDefault();
          var card = btn.closest('[data-dismissible]');
          if (!card) return;

          var insightId = card.getAttribute('data-dismissible');

          card.style.opacity = '0';
          card.style.transform = 'translateY(-10px)';
          card.style.transition = 'opacity 0.3s ease, transform 0.3s ease';

          setTimeout(function () {
            card.style.display = 'none';
            saveDismissed(insightId);
          }, 300);
        });
      });
    }
  });
})();
