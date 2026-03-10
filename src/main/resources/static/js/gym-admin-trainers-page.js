function showUpdateNotesModal(button) {
    const requestId = button.getAttribute('data-request-id');
    const trainerName = button.getAttribute('data-trainer-name');
    const currentNotes = button.getAttribute('data-current-notes');
    
    document.getElementById('modalTrainerName').textContent = trainerName;
    document.getElementById('modalNotes').value = currentNotes || '';
    document.getElementById('updateNotesForm').action = '/gym/admin/trainers/' + requestId + '/update-notes';
    
    document.getElementById('updateNotesModal').classList.remove('hidden');
    document.getElementById('updateNotesModal').classList.add('flex');
}

function hideUpdateNotesModal() {
    document.getElementById('updateNotesModal').classList.add('hidden');
    document.getElementById('updateNotesModal').classList.remove('flex');
}

// Auto-open modal if server provided data
(function() {
    var data = window.gymTrainersData || {};
    if (data.updateNotesRequestId !== null && data.updateNotesRequestId !== undefined) {
        document.getElementById('modalTrainerName').textContent = data.updateNotesTrainerName || '';
        document.getElementById('modalNotes').value = data.updateNotesNotes || '';
        document.getElementById('updateNotesForm').action = '/gym/admin/trainers/' + data.updateNotesRequestId + '/update-notes';
        document.getElementById('updateNotesModal').classList.remove('hidden');
        document.getElementById('updateNotesModal').classList.add('flex');
    }
})();

// Close modal on background click
document.getElementById('updateNotesModal').addEventListener('click', function(e) {
    if (e.target === this) {
        hideUpdateNotesModal();
    }
});

// Close modal on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        hideUpdateNotesModal();
    }
});
