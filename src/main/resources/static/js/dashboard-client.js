(function () {
  'use strict';

  var STORAGE_KEY = 'cd_weekly_target';
  var DEFAULT_TARGET = 3;

  document.addEventListener('DOMContentLoaded', function () {
    initTimelineBars();
    initDayTooltips();
    initWeeklyProgress();
    initTargetEditor();
  });

  /* Compute proportional activity bar widths */
  function initTimelineBars() {
    var days = document.querySelectorAll('.cd-day[data-activity]');
    if (!days.length) return;

    var counts = Array.prototype.slice.call(days).map(function (d) {
      return parseInt(d.getAttribute('data-activity'), 10) || 0;
    });
    var max = Math.max.apply(null, counts.concat([1])); // avoid div-by-zero

    days.forEach(function (day, i) {
      var bar = day.querySelector('.cd-day__bar');
      if (!bar) return;
      var pct = Math.round((counts[i] / max) * 100);
      // Defer to allow CSS transition to animate on load
      setTimeout(function () {
        bar.style.width = pct + '%';
      }, 300 + i * 40);
    });
  }

  /* Weekly progress: stars and target management */
  function initWeeklyProgress() {
    var starsEl = document.getElementById('cd-progress-stars');
    var targetValueEl = document.getElementById('cd-target-value');
    if (!starsEl) return;

    var completed = parseInt(starsEl.getAttribute('data-completed'), 10) || 0;
    var target = loadTarget();

    renderStars(starsEl, completed, target);
    if (targetValueEl) targetValueEl.textContent = target;
  }

  function renderStars(container, completed, target) {
    container.innerHTML = '';
    var total = Math.max(target, completed); // show extra stars if completed > target
    for (var i = 0; i < total; i++) {
      var star = document.createElement('span');
      star.className = 'cd-progress-star';
      star.setAttribute('aria-hidden', 'true');
      if (i < completed && i < target) {
        // filled within target
        star.classList.add('cd-progress-star--filled');
        star.textContent = '⭐';
        star.style.animationDelay = (i * 80) + 'ms';
      } else if (i < completed) {
        // bonus beyond target — gold/done
        star.classList.add('cd-progress-star--done');
        star.textContent = '🌟';
        star.style.animationDelay = (i * 80) + 'ms';
      } else {
        // unfilled
        star.textContent = '☆';
      }
      container.appendChild(star);
    }

    // Show a completion badge if target met
    if (completed >= target && target > 0) {
      var badge = document.createElement('span');
      badge.className = 'cd-progress-star cd-progress-star--done';
      badge.textContent = '🎉';
      badge.style.animationDelay = (total * 80) + 'ms';
      badge.setAttribute('title', 'Weekly target achieved!');
      container.appendChild(badge);
    }
  }

  /* Target editor toggle */
  function initTargetEditor() {
    var editBtn = document.getElementById('cd-target-edit-btn');
    var editor = document.getElementById('cd-target-editor');
    var input = document.getElementById('cd-target-input');
    var saveBtn = document.getElementById('cd-target-save-btn');
    var targetValueEl = document.getElementById('cd-target-value');
    var starsEl = document.getElementById('cd-progress-stars');

    if (!editBtn || !editor) return;

    var target = loadTarget();
    if (input) input.value = target;

    editBtn.addEventListener('click', function () {
      var hidden = editor.hasAttribute('hidden');
      if (hidden) {
        editor.removeAttribute('hidden');
        editBtn.setAttribute('aria-expanded', 'true');
        if (input) input.focus();
      } else {
        editor.setAttribute('hidden', '');
        editBtn.setAttribute('aria-expanded', 'false');
      }
    });

    if (saveBtn && input) {
      saveBtn.addEventListener('click', function () {
        var val = parseInt(input.value, 10);
        if (isNaN(val) || val < 1) val = 1;
        if (val > 14) val = 14;
        input.value = val;
        saveTarget(val);

        if (targetValueEl) targetValueEl.textContent = val;
        if (starsEl) {
          var completed = parseInt(starsEl.getAttribute('data-completed'), 10) || 0;
          renderStars(starsEl, completed, val);
        }

        editor.setAttribute('hidden', '');
        editBtn.setAttribute('aria-expanded', 'false');
      });
    }
  }

  function loadTarget() {
    try {
      var stored = localStorage.getItem(STORAGE_KEY);
      if (stored !== null) {
        var val = parseInt(stored, 10);
        if (!isNaN(val) && val >= 1 && val <= 14) return val;
      }
    } catch (e) { /* localStorage may be unavailable */ }
    return DEFAULT_TARGET;
  }

  function saveTarget(val) {
    try {
      localStorage.setItem(STORAGE_KEY, String(val));
    } catch (e) { /* ignore */ }
  }

  /* Lightweight tooltip showing task/workout breakdown on hover */
  function initDayTooltips() {
    var days = document.querySelectorAll('.cd-day[data-tasks]');
    if (!days.length) return;

    var tip = document.createElement('div');
    tip.className = 'cd-day-tooltip';
    tip.setAttribute('role', 'tooltip');
    tip.setAttribute('aria-hidden', 'true');
    document.body.appendChild(tip);

    days.forEach(function (day) {
      day.addEventListener('mouseenter', function (e) {
        var tasks = parseInt(day.getAttribute('data-tasks'), 10) || 0;
        var workouts = parseInt(day.getAttribute('data-workouts'), 10) || 0;

        if (tasks === 0 && workouts === 0) {
          tip.innerHTML = '<span class="cd-tip-empty">Free day</span>';
        } else {
          var parts = [];
          if (tasks > 0) {
            parts.push('<span class="cd-tip-task">\u25cf ' + tasks + ' task' + (tasks !== 1 ? 's' : '') + '</span>');
          }
          if (workouts > 0) {
            parts.push('<span class="cd-tip-workout">\u25c6 ' + workouts + ' workout' + (workouts !== 1 ? 's' : '') + '</span>');
          }
          tip.innerHTML = parts.join('');
        }

        positionTip(e);
        tip.classList.add('cd-day-tooltip--visible');
      });

      day.addEventListener('mousemove', function (e) {
        positionTip(e);
      });

      day.addEventListener('mouseleave', function () {
        tip.classList.remove('cd-day-tooltip--visible');
      });
    });

    function positionTip(e) {
      var x = e.clientX;
      var y = e.clientY;
      var tw = tip.offsetWidth;
      var left = x - tw / 2;
      if (left < 8) left = 8;
      if (left + tw > window.innerWidth - 8) left = window.innerWidth - tw - 8;
      tip.style.left = left + 'px';
      tip.style.top = (y - tip.offsetHeight - 10) + 'px';
    }
  }
})();
