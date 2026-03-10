function showReportModal(button) {
    const reviewId = button.getAttribute('data-review-id');
    const trainerId = button.getAttribute('data-trainer-id');
    const form = document.getElementById('reportForm');
    form.action = '/reviews/' + encodeURIComponent(reviewId) + '/report';
    document.getElementById('reportTrainerId').value = trainerId;
    document.getElementById('reportModal').classList.remove('hidden');
}

function closeReportModal() {
    document.getElementById('reportModal').classList.add('hidden');
}

const copyBtn = document.getElementById('copyTrainerCode');
if (copyBtn) {
    copyBtn.addEventListener('click', () => {
        const code = document.getElementById('trainerCodeVal').textContent.trim();
        navigator.clipboard.writeText(code).then(() => {
            const label = document.getElementById('copyTrainerCodeLabel');
            label.textContent = 'Copied!';
            setTimeout(() => { label.textContent = 'Copy'; }, 2000);
        });
    });
}
