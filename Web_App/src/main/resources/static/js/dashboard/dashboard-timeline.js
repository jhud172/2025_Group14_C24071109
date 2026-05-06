/**
 * Dashboard Timeline Component
 * 12-month horizontal activity timeline with animations, tooltips, and accessibility
 */

(function () {
    'use strict';

    const initWeekStripHoverDetails = () => {
        const cards = document.querySelectorAll('[aria-label="Week strip"] a[data-day-label]');
        if (!cards.length) return;

        let tooltip = document.getElementById('week-strip-tooltip');
        if (!tooltip) {
            tooltip = document.createElement('div');
            tooltip.id = 'week-strip-tooltip';
            tooltip.className = 'fixed z-50 pointer-events-none hidden max-w-72 rounded-xl border border-cyber-700 bg-cyber-900/95 px-3 py-2 text-xs text-slate-200 shadow-xl';
            document.body.appendChild(tooltip);
        }

        let detailTimer = null;

        const renderTooltip = (card, secondaryVisible) => {
            const dayLabel = card.dataset.dayLabel || 'Day';
            const prettyDate = card.dataset.prettyDate || '';
            const taskCount = parseInt(card.dataset.taskCount || '0', 10);
            const workoutCount = parseInt(card.dataset.workoutCount || '0', 10);

            const taskNames = taskCount > 0
                ? Array.from({ length: Math.min(taskCount, 2) }, (_, index) => `Task ${index + 1}`).join(', ')
                : 'No tasks';
            const workoutNames = workoutCount > 0
                ? Array.from({ length: Math.min(workoutCount, 2) }, (_, index) => `Workout ${index + 1}`).join(', ')
                : 'No workouts';

            tooltip.innerHTML = `
                <div class="font-semibold text-neon-cyan">${dayLabel} ${prettyDate ? '· ' + prettyDate : ''}</div>
                <div class="mt-1 text-slate-300">${taskCount} tasks · ${workoutCount} workouts</div>
                <div class="mt-1 text-[11px] text-slate-400 ${secondaryVisible ? '' : 'opacity-0 h-0 overflow-hidden'}">
                    Tasks: ${taskNames}<br/>Workouts: ${workoutNames}
                </div>
            `;
        };

        const positionTooltip = (event) => {
            const offset = 14;
            tooltip.style.left = `${event.clientX + offset}px`;
            tooltip.style.top = `${event.clientY + offset}px`;
        };

        cards.forEach((card) => {
            card.addEventListener('mouseenter', (event) => {
                clearTimeout(detailTimer);
                renderTooltip(card, false);
                tooltip.classList.remove('hidden');
                positionTooltip(event);

                detailTimer = setTimeout(() => {
                    renderTooltip(card, true);
                }, 420);
            });

            card.addEventListener('mousemove', (event) => {
                positionTooltip(event);
            });

            card.addEventListener('mouseleave', () => {
                clearTimeout(detailTimer);
                tooltip.classList.add('hidden');
            });
        });
    };

    const initDayTimeline = () => {
        const section = document.getElementById('dashboard-day-timeline');
        if (!section) return;

        const track = section.querySelector('[data-day-timeline-track]');
        const scrollArea = section.querySelector('[data-day-timeline-scroll]');
        const prevBtn = section.querySelector('[data-day-prev]');
        const nextBtn = section.querySelector('[data-day-next]');
        const label = section.querySelector('[data-day-timeline-label]');
        const subtitle = section.querySelector('[data-day-timeline-subtitle]');
        const hint = section.querySelector('[data-day-timeline-hint]');

        if (!track || !scrollArea || !prevBtn || !nextBtn || !label || !subtitle || !hint) return;

        const days = Array.from(document.querySelectorAll('#dashboard-day-timeline-data .dashboard-day-data')).map((node) => ({
            dayLabel: node.dataset.dayLabel || 'Day',
            prettyDate: node.dataset.prettyDate || '',
            taskCount: parseInt(node.dataset.taskCount || '0', 10),
            workoutCount: parseInt(node.dataset.workoutCount || '0', 10),
            today: node.dataset.today === 'true'
        }));

        if (!days.length) return;

        let index = Math.max(days.findIndex((day) => day.today), 0);

        const createHourLabel = (hour) => `${String(hour).padStart(2, '0')}:00`;

        const seededSlots = (day) => {
            const slots = [];
            const total = day.taskCount + day.workoutCount;
            if (total === 0) return slots;

            const taskHours = [9, 11, 14, 16, 18];
            const workoutHours = [6, 8, 12, 17, 19, 20];

            for (let taskIndex = 0; taskIndex < day.taskCount; taskIndex += 1) {
                const hour = taskHours[taskIndex % taskHours.length];
                slots.push({ type: 'task', title: `Task ${taskIndex + 1}`, hour, minute: 15 });
            }
            for (let workoutIndex = 0; workoutIndex < day.workoutCount; workoutIndex += 1) {
                const hour = workoutHours[workoutIndex % workoutHours.length];
                slots.push({ type: 'workout', title: `Workout ${workoutIndex + 1}`, hour, minute: 40 });
            }

            return slots.sort((first, second) => (first.hour * 60 + first.minute) - (second.hour * 60 + second.minute));
        };

        const densityForHour = (hour, slots) => {
            const count = slots.filter((slot) => slot.hour === hour).length;
            if (count >= 2) return 'bg-neon-green/20';
            if (count === 1) return 'bg-neon-cyan/10';
            return 'bg-transparent';
        };

        const renderDay = (day) => {
            const slots = seededSlots(day);
            const now = new Date();
            const isToday = day.today;
            const totalMinutes = isToday ? now.getHours() * 60 + now.getMinutes() : null;

            label.textContent = day.today ? 'Today' : `${day.dayLabel}`;
            subtitle.textContent = day.prettyDate;
            hint.textContent = slots.length
                ? 'Hover activity chips for item names. Density tint marks busy hours.'
                : 'No scheduled items for this day. Add items in Calendar to populate timeline.';

            track.classList.add('opacity-0', 'translate-y-1');

            window.setTimeout(() => {
                const hourColumns = Array.from({ length: 24 }, (_, hour) => {
                    const startMinutes = hour * 60;
                    const hourSlots = slots.filter((slot) => slot.hour === hour);
                    const chips = hourSlots.map((slot) => {
                        const chipClass = slot.type === 'task'
                            ? 'border-neon-cyan/40 bg-neon-cyan/10 text-neon-cyan'
                            : 'border-neon-purple/40 bg-neon-purple/10 text-neon-purple';
                        return `<div class="mt-1 rounded-md border px-2 py-1 text-[10px] font-medium ${chipClass}" title="${slot.title}">${slot.title}</div>`;
                    }).join('');

                    return `
                        <div class="relative min-w-[50px] border-r border-cyber-800/70 px-1 ${densityForHour(hour, slots)}">
                            <div class="text-[10px] font-semibold text-slate-500">${createHourLabel(hour)}</div>
                            <div class="mt-1 min-h-14">${chips}</div>
                            <div class="mt-1 h-10 rounded-sm border border-dashed border-cyber-800/70"></div>
                        </div>
                    `;
                }).join('');

                const nowLine = isToday && totalMinutes !== null
                    ? `<div class="pointer-events-none absolute top-4 bottom-4 w-0.5 bg-neon-green shadow-[0_0_8px_rgba(52,211,153,0.7)]" style="left: calc(${(totalMinutes / (24 * 60)) * 100}% - 1px);">
                            <div class="-ml-3 -mt-1 rounded-full bg-neon-green px-2 py-0.5 text-[10px] font-bold text-cyber-900">Now</div>
                       </div>`
                    : '';

                track.innerHTML = `
                    <div class="relative">
                        ${nowLine}
                        <div class="flex min-w-[1200px]">${hourColumns}</div>
                    </div>
                `;

                track.classList.remove('opacity-0', 'translate-y-1');
                track.classList.add('transition-all', 'duration-200');

                if (isToday && totalMinutes !== null) {
                    const target = Math.max(0, ((totalMinutes / (24 * 60)) * scrollArea.scrollWidth) - (scrollArea.clientWidth / 2));
                    scrollArea.scrollTo({ left: target, behavior: 'smooth' });
                } else {
                    scrollArea.scrollTo({ left: 0, behavior: 'smooth' });
                }
            }, 120);

            prevBtn.disabled = index === 0;
            nextBtn.disabled = index === days.length - 1;
            prevBtn.classList.toggle('opacity-40', prevBtn.disabled);
            nextBtn.classList.toggle('opacity-40', nextBtn.disabled);
        };

        const goTo = (nextIndex) => {
            if (nextIndex < 0 || nextIndex >= days.length || nextIndex === index) return;
            index = nextIndex;
            renderDay(days[index]);
        };

        prevBtn.addEventListener('click', () => goTo(index - 1));
        nextBtn.addEventListener('click', () => goTo(index + 1));

        let touchStart = 0;
        section.addEventListener('touchstart', (event) => {
            touchStart = event.changedTouches[0].screenX;
        }, { passive: true });

        section.addEventListener('touchend', (event) => {
            const touchEnd = event.changedTouches[0].screenX;
            const delta = touchStart - touchEnd;
            if (Math.abs(delta) < 50) return;
            if (delta > 0) goTo(index + 1);
            if (delta < 0) goTo(index - 1);
        }, { passive: true });

        renderDay(days[index]);
    };

    // Fetch timeline data from backend API
    const fetchTimelineData = async () => {
        try {
            const response = await fetch('/api/dashboard/timeline-data', {
                method: 'GET',
                headers: {
                    'Content-Type': 'application/json'
                },
                credentials: 'include' // Include auth cookies
            });
            
            if (!response.ok) {
                throw new Error(`Timeline API error: ${response.status}`);
            }
            
            const apiData = await response.json();
            
            // Transform API response to match timeline data structure
            return apiData.map((month, index) => ({
                month: month.month,
                monthShort: month.month,
                year: new Date().getFullYear(),
                totalLogs: month.totalLogs,
                totalSessions: month.totalSessions,
                totalTasks: month.totalTasks,
                totalActivity: month.totalLogs + month.totalSessions + month.totalTasks,
                isCurrent: index === apiData.length - 1 // Last item is current month
            }));
        } catch (error) {
            console.error('Failed to fetch timeline data:', error);
            // Return empty array if API fails - component will handle gracefully
            return [];
        }
    };

    // Determine activity level and color class
    const getActivityLevel = (totalActivity) => {
        if (totalActivity === 0) return { level: 'none', classes: 'bg-cyber-900 border border-dashed border-cyber-700', shadow: '' };
        if (totalActivity >= 15) return { level: 'high', classes: 'bg-neon-green', shadow: 'shadow-lg shadow-neon-green/30' };
        if (totalActivity >= 5) return { level: 'medium', classes: 'bg-neon-cyan', shadow: 'shadow-md shadow-neon-cyan/25' };
        return { level: 'low', classes: 'bg-cyber-700', shadow: '' };
    };

    // Create tooltip element
    const createTooltip = () => {
        const tooltip = document.createElement('div');
        tooltip.id = 'timeline-tooltip';
        tooltip.className = 'absolute z-50 hidden pointer-events-none';
        tooltip.innerHTML = `
            <div class="card-neon card--cyan min-w-48 p-3 shadow-xl">
                <div class="text-sm font-semibold text-slate-200 mb-2" data-tooltip-month></div>
                <div class="space-y-1 text-xs">
                    <div class="flex justify-between">
                        <span class="text-slate-400">Logs:</span>
                        <span class="font-medium text-neon-green" data-tooltip-logs>0</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-slate-400">Sessions:</span>
                        <span class="font-medium text-neon-cyan" data-tooltip-sessions>0</span>
                    </div>
                    <div class="flex justify-between">
                        <span class="text-slate-400">Tasks:</span>
                        <span class="font-medium text-neon-purple" data-tooltip-tasks>0</span>
                    </div>
                    <div class="flex justify-between pt-1 mt-1 border-t border-cyber-700">
                        <span class="text-slate-400">Total:</span>
                        <span class="font-semibold text-slate-200" data-tooltip-total>0</span>
                    </div>
                </div>
            </div>
        `;
        document.body.appendChild(tooltip);
        return tooltip;
    };

    // Position tooltip above segment
    const positionTooltip = (tooltip, segment) => {
        const rect = segment.getBoundingClientRect();
        const scrollY = window.pageYOffset || document.documentElement.scrollTop;
        const scrollX = window.pageXOffset || document.documentElement.scrollLeft;
        
        tooltip.style.left = `${rect.left + scrollX + (rect.width / 2)}px`;
        tooltip.style.top = `${rect.top + scrollY - 10}px`;
        tooltip.style.transform = 'translate(-50%, -100%)';
    };

    // Update tooltip content
    const updateTooltip = (tooltip, data) => {
        tooltip.querySelector('[data-tooltip-month]').textContent = data.month;
        tooltip.querySelector('[data-tooltip-logs]').textContent = data.totalLogs;
        tooltip.querySelector('[data-tooltip-sessions]').textContent = data.totalSessions;
        tooltip.querySelector('[data-tooltip-tasks]').textContent = data.totalTasks;
        tooltip.querySelector('[data-tooltip-total]').textContent = data.totalActivity;
    };

    // Show tooltip
    const showTooltip = (tooltip, segment, data) => {
        updateTooltip(tooltip, data);
        positionTooltip(tooltip, segment);
        tooltip.classList.remove('hidden');
    };

    // Hide tooltip
    const hideTooltip = (tooltip) => {
        tooltip.classList.add('hidden');
    };

    // Create timeline segment
    const createSegment = (data, index, tooltip) => {
        const { classes, shadow } = getActivityLevel(data.totalActivity);
        
        const segment = document.createElement('div');
        segment.className = `timeline-segment relative flex-1 min-w-16 cursor-pointer group transition-all duration-300 ${classes} ${shadow}`;
        segment.setAttribute('role', 'button');
        segment.setAttribute('tabindex', '0');
        segment.setAttribute('aria-label', `${data.month}: ${data.totalActivity} activities (${data.totalLogs} logs, ${data.totalSessions} sessions, ${data.totalTasks} tasks)`);
        segment.dataset.month = data.month;
        segment.dataset.index = index;
        
        // Add current month indicator
        if (data.isCurrent) {
            segment.classList.add('ring-2', 'ring-neon-cyan');
            segment.innerHTML = `
                <div class="absolute -bottom-6 left-1/2 -translate-x-1/2 text-[10px] font-semibold text-neon-cyan whitespace-nowrap">
                    NOW
                </div>
            `;
        }
        
        // Set initial scale to 0 for animation
        segment.style.transform = 'scaleY(0)';
        segment.style.transformOrigin = 'bottom';
        segment.style.opacity = '0';
        
        // Hover effects
        segment.addEventListener('mouseenter', () => {
            showTooltip(tooltip, segment, data);
            segment.classList.add('scale-105', 'z-10');
        });
        
        segment.addEventListener('mouseleave', () => {
            hideTooltip(tooltip);
            segment.classList.remove('scale-105', 'z-10');
        });
        
        // Touch support
        segment.addEventListener('touchstart', (e) => {
            e.preventDefault();
            showTooltip(tooltip, segment, data);
            segment.classList.add('scale-105', 'z-10');
            
            // Hide tooltip after 2 seconds on touch
            setTimeout(() => {
                hideTooltip(tooltip);
                segment.classList.remove('scale-105', 'z-10');
            }, 2000);
        }, { passive: false });
        
        // Focus handling
        segment.addEventListener('focus', () => {
            showTooltip(tooltip, segment, data);
        });
        
        segment.addEventListener('blur', () => {
            hideTooltip(tooltip);
        });
        
        return segment;
    };

    // Animate segments sequentially
    const animateSegments = (segments) => {
        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        
        segments.forEach((segment, index) => {
            const delay = prefersReducedMotion ? 0 : index * 50;
            
            setTimeout(() => {
                segment.style.transition = prefersReducedMotion ? 'none' : 'transform 400ms cubic-bezier(0.34, 1.56, 0.64, 1), opacity 400ms ease-out';
                segment.style.transform = 'scaleY(1)';
                segment.style.opacity = '1';
            }, delay);
        });
    };

    // Keyboard navigation
    const setupKeyboardNavigation = (container, segments) => {
        let currentIndex = segments.length - 1; // Start at current month
        
        container.addEventListener('keydown', (e) => {
            if (!['ArrowLeft', 'ArrowRight', 'Home', 'End'].includes(e.key)) return;
            
            e.preventDefault();
            
            switch (e.key) {
                case 'ArrowLeft':
                    currentIndex = Math.max(0, currentIndex - 1);
                    break;
                case 'ArrowRight':
                    currentIndex = Math.min(segments.length - 1, currentIndex + 1);
                    break;
                case 'Home':
                    currentIndex = 0;
                    break;
                case 'End':
                    currentIndex = segments.length - 1;
                    break;
            }
            
            segments[currentIndex].focus();
        });
    };

    // Smooth scroll functionality
    const setupScrollControls = (container, scrollWrapper) => {
        const scrollLeft = container.querySelector('[data-scroll-left]');
        const scrollRight = container.querySelector('[data-scroll-right]');
        
        if (!scrollLeft || !scrollRight) return;
        
        const updateScrollButtons = () => {
            const { scrollLeft: pos, scrollWidth, clientWidth } = scrollWrapper;
            
            if (pos <= 0) {
                scrollLeft.disabled = true;
                scrollLeft.classList.add('opacity-30', 'cursor-not-allowed');
            } else {
                scrollLeft.disabled = false;
                scrollLeft.classList.remove('opacity-30', 'cursor-not-allowed');
            }
            
            if (pos + clientWidth >= scrollWidth - 1) {
                scrollRight.disabled = true;
                scrollRight.classList.add('opacity-30', 'cursor-not-allowed');
            } else {
                scrollRight.disabled = false;
                scrollRight.classList.remove('opacity-30', 'cursor-not-allowed');
            }
        };
        
        scrollLeft.addEventListener('click', () => {
            scrollWrapper.scrollBy({ left: -200, behavior: 'smooth' });
        });
        
        scrollRight.addEventListener('click', () => {
            scrollWrapper.scrollBy({ left: 200, behavior: 'smooth' });
        });
        
        scrollWrapper.addEventListener('scroll', updateScrollButtons);
        updateScrollButtons();
        
        // Auto-scroll to current month on load
        const currentSegment = scrollWrapper.querySelector('[data-index="11"]'); // Last segment (current)
        if (currentSegment) {
            setTimeout(() => {
                currentSegment.scrollIntoView({ behavior: 'smooth', inline: 'center', block: 'nearest' });
            }, 600); // After animation completes
        }
    };

    // Initialize timeline
    const initTimeline = async () => {
        const container = document.getElementById('dashboard-timeline');
        if (!container) return;
        
        const scrollWrapper = container.querySelector('[data-timeline-scroll]');
        const segmentsContainer = container.querySelector('[data-timeline-segments]');
        
        if (!scrollWrapper || !segmentsContainer) return;
        
        // Fetch data from backend API
        const timelineData = await fetchTimelineData();
        
        // Handle empty data
        if (timelineData.length === 0) {
            segmentsContainer.innerHTML = '<p class="text-slate-400">No activity data available</p>';
            return;
        }
        
        const tooltip = createTooltip();
        
        // Create segments
        const segments = timelineData.map((data, index) => {
            const segment = createSegment(data, index, tooltip);
            segmentsContainer.appendChild(segment);
            return segment;
        });
        
        // Setup keyboard navigation
        setupKeyboardNavigation(container, segments);
        
        // Setup scroll controls
        setupScrollControls(container, scrollWrapper);
        
        // Animate on intersection
        const prefersReducedMotion = window.matchMedia('(prefers-reduced-motion: reduce)').matches;
        
        if (prefersReducedMotion || !('IntersectionObserver' in window)) {
            animateSegments(segments);
        } else {
            const observer = new IntersectionObserver((entries) => {
                entries.forEach((entry) => {
                    if (entry.isIntersecting) {
                        animateSegments(segments);
                        observer.unobserve(entry.target);
                    }
                });
            }, { threshold: 0.2, rootMargin: '0px 0px -10% 0px' });
            
            observer.observe(container);
        }
        
        // Touch swipe support
        let touchStartX = 0;
        let touchEndX = 0;
        
        scrollWrapper.addEventListener('touchstart', (e) => {
            touchStartX = e.changedTouches[0].screenX;
        }, { passive: true });
        
        scrollWrapper.addEventListener('touchend', (e) => {
            touchEndX = e.changedTouches[0].screenX;
            const diff = touchStartX - touchEndX;
            
            if (Math.abs(diff) > 50) { // Minimum swipe distance
                scrollWrapper.scrollBy({ left: diff, behavior: 'smooth' });
            }
        }, { passive: true });
    };

    // Initialize on DOM ready
    if (document.readyState === 'loading') {
        document.addEventListener('DOMContentLoaded', () => {
            initWeekStripHoverDetails();
            initDayTimeline();
            initTimeline();
        });
    } else {
        initWeekStripHoverDetails();
        initDayTimeline();
        initTimeline();
    }
})();
