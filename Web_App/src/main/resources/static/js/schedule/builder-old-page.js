// Enhanced stats tracking
function updateScheduleStats() {
    const allWorkouts = document.querySelectorAll('.schedule-drop-zone > div');
    const totalWorkouts = allWorkouts.length;
    
    const daysWithWorkouts = new Set();
    document.querySelectorAll('.schedule-drop-zone').forEach(zone => {
        if (zone.children.length > 0) {
            daysWithWorkouts.add(zone.closest('[data-day]').dataset.day);
        }
    });
    
    const activeDays = daysWithWorkouts.size;
    const restDays = 7 - activeDays;
    const avgPerDay = activeDays > 0 ? (totalWorkouts / activeDays).toFixed(1) : '0.0';
    
    document.getElementById('totalWorkouts').textContent = totalWorkouts;
    document.getElementById('activeDays').textContent = activeDays;
    document.getElementById('restDays').textContent = restDays;
    document.getElementById('avgPerDay').textContent = avgPerDay;
    
    // Update day counts
    document.querySelectorAll('.schedule-day-column').forEach(column => {
        const dropZone = column.querySelector('.schedule-drop-zone');
        const countBadge = column.querySelector('.schedule-day-count');
        const count = dropZone.children.length;
        
        countBadge.textContent = count;
        countBadge.dataset.count = count;
        
        if (count > 0) {
            dropZone.classList.add('has-workouts');
        } else {
            dropZone.classList.remove('has-workouts');
        }
    });
}

// Call on page load and after any drag/drop
document.addEventListener('DOMContentLoaded', function() {
    updateScheduleStats();
    
    // Add mutation observer to track changes
    document.querySelectorAll('.schedule-drop-zone').forEach(zone => {
        const observer = new MutationObserver(updateScheduleStats);
        observer.observe(zone, { childList: true });
    });
});
