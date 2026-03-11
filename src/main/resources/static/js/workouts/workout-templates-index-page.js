(function () {
    const csrfEl = document.getElementById('csrf-token');
    document.querySelectorAll('.set-preferred-btn').forEach(btn => {
        btn.addEventListener('click', function () {
            const id = this.getAttribute('data-template-id');
            const csrf = csrfEl ? csrfEl.value : null;
            const headers = { 'Content-Type': 'application/x-www-form-urlencoded', 'X-Requested-With': 'XMLHttpRequest' };
            if (csrf) headers['X-CSRF-TOKEN'] = csrf;
            fetch('/workout-templates/' + id + '/set-preferred', { method: 'POST', headers })
                .then(r => {
                    if (r.ok) {
                        location.reload();
                    } else {
                        console.error('Failed to set preferred template, status: ' + r.status);
                    }
                })
                .catch(err => {
                    console.error('Network error setting preferred template:', err);
                });
        });
    });
})();
