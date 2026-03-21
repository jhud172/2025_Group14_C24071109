(function () {
    const reportModal = document.getElementById('reportModal');
    const reportForm = document.getElementById('reportForm');
    const trainerIdInput = document.getElementById('reportTrainerId');

    function openReportModal(button) {
        if (!reportModal || !reportForm || !trainerIdInput) return;

        const reviewId = button.getAttribute('data-review-id');
        const trainerId = button.getAttribute('data-trainer-id');

        reportForm.action = '/reviews/' + encodeURIComponent(reviewId) + '/report';
        trainerIdInput.value = trainerId || '';
        reportModal.classList.remove('hidden');
    }

    function closeReportModal() {
        reportModal?.classList.add('hidden');
    }

    document.querySelectorAll('[data-open-report-modal]').forEach((button) => {
        button.addEventListener('click', () => openReportModal(button));
    });

    document.querySelectorAll('[data-close-report-modal]').forEach((button) => {
        button.addEventListener('click', closeReportModal);
    });

    reportModal?.addEventListener('click', (event) => {
        if (event.target === reportModal) {
            closeReportModal();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape') {
            closeReportModal();
        }
    });

    const copyBtn = document.getElementById('copyTrainerCode');
    if (copyBtn) {
        copyBtn.addEventListener('click', () => {
            const code = document.getElementById('trainerCodeVal')?.textContent?.trim();
            if (!code) return;

            navigator.clipboard.writeText(code).then(() => {
                const label = document.getElementById('copyTrainerCodeLabel');
                if (!label) return;

                label.textContent = 'Copied!';
                setTimeout(() => {
                    label.textContent = 'Copy';
                }, 2000);
            });
        });
    }
}());
