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
