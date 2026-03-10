(function () {
    const btn = document.getElementById('toggleOptional');
    const panel = document.getElementById('optionalFields');
    if (!btn || !panel) return;
    btn.addEventListener('click', function () {
        const hidden = panel.classList.toggle('hidden');
        btn.textContent = hidden
            ? '+ Show optional fields (pulse, arm, position, notes)'
            : '− Hide optional fields';
    });
})();
