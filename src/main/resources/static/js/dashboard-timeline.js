/**
 * Dashboard Timeline Component
 * 12-month horizontal activity timeline with animations, tooltips, and accessibility
 */

(function () {
    'use strict';

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
            // Fall back to empty array - component will handle gracefully
            return [];
        }
    };

    // Mock data structure (12 months) - fallback if API fails
    const generateTimelineData = async () => {
        // First try to fetch real data from backend
        const apiData = await fetchTimelineData();
        
        if (apiData.length > 0) {
            return apiData;
        }
        
        // Fallback to mock data if API fails
        const months = [
            'Jan', 'Feb', 'Mar', 'Apr', 'May', 'Jun',
            'Jul', 'Aug', 'Sep', 'Oct', 'Nov', 'Dec'
        ];
        const currentDate = new Date();
        const currentMonth = currentDate.getMonth();
        const currentYear = currentDate.getFullYear();
        const data = [];

        for (let i = 11; i >= 0; i--) {
            const monthIndex = (currentMonth - i + 12) % 12;
            const year = currentMonth - i < 0 ? currentYear - 1 : currentYear;
            const monthName = `${months[monthIndex]} ${year}`;
            
            // Generate random mock data (replace with real data from backend)
            const totalLogs = Math.floor(Math.random() * 25);
            const totalSessions = Math.floor(Math.random() * 15);
            const totalTasks = Math.floor(Math.random() * 30);
            const totalActivity = totalLogs + totalSessions + totalTasks;

            data.push({
                month: monthName,
                monthShort: months[monthIndex],
                year: year,
                totalLogs: totalLogs,
                totalSessions: totalSessions,
                totalTasks: totalTasks,
                totalActivity: totalActivity,
                isCurrent: i === 0
            });
        }

        return data;
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
        
        // Generate/fetch data and create tooltip
        const timelineData = await generateTimelineData();
        
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
        document.addEventListener('DOMContentLoaded', initTimeline);
    } else {
        initTimeline();
    }
})();
