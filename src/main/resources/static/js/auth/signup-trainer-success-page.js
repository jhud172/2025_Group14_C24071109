var code = (typeof trainerCodeData !== 'undefined') ? trainerCodeData : '';
const copyBtn = document.getElementById('copyCodeBtn');
const copyLabel = document.getElementById('copyLabel');
const copyIcon = document.getElementById('copyIcon');

if (copyBtn && code) {
    copyBtn.addEventListener('click', () => {
        navigator.clipboard.writeText(code).then(() => {
            copyLabel.textContent = 'Copied!';
            copyIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M5 13l4 4L19 7" />';
            setTimeout(() => {
                copyLabel.textContent = 'Copy code';
                copyIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M8 16H6a2 2 0 01-2-2V6a2 2 0 012-2h8a2 2 0 012 2v2m-6 12h8a2 2 0 002-2v-8a2 2 0 00-2-2h-8a2 2 0 00-2 2v8a2 2 0 002 2z" />';
            }, 2000);
        });
    });
}
