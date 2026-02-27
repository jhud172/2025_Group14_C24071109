(function () {
  'use strict';

  document.addEventListener('DOMContentLoaded', function () {
    initTimelineBars();
    initDayTooltips();
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
