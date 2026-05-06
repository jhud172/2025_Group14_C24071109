(function () {
    const modal = document.getElementById('updateNotesModal');
    const form = document.getElementById('updateNotesForm');
    const trainerName = document.getElementById('modalTrainerName');
    const notes = document.getElementById('modalNotes');

    if (!modal || !form || !trainerName || !notes) return;

    function openModal(requestId, displayName, currentNotes) {
        trainerName.textContent = displayName || '';
        notes.value = currentNotes || '';
        form.action = '/gym/admin/trainers/' + encodeURIComponent(requestId) + '/update-notes';
        modal.classList.remove('hidden');
        modal.classList.add('flex');
    }

    function closeModal() {
        modal.classList.add('hidden');
        modal.classList.remove('flex');
    }

    document.querySelectorAll('[data-open-update-notes]').forEach((button) => {
        button.addEventListener('click', () => {
            openModal(
                button.getAttribute('data-request-id'),
                button.getAttribute('data-trainer-name'),
                button.getAttribute('data-current-notes')
            );
        });
    });

    document.querySelectorAll('[data-close-update-notes]').forEach((button) => {
        button.addEventListener('click', closeModal);
    });

    const bootstrapRequestId = modal.dataset.requestId;
    if (bootstrapRequestId) {
        openModal(
            bootstrapRequestId,
            modal.dataset.trainerName,
            modal.dataset.currentNotes
        );
    }

    modal.addEventListener('click', (event) => {
        if (event.target === modal) {
            closeModal();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeModal();
        }
    });
}());
