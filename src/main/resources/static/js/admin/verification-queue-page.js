function selectRequest(row) {
    const name = row.getAttribute('data-trainer-name') || 'Unknown';
    const email = row.getAttribute('data-trainer-email') || '';
    const status = row.getAttribute('data-status') || 'PENDING';
    const notes = row.getAttribute('data-notes') || '—';
    const adminNotes = row.getAttribute('data-admin-notes') || '—';
    const submitted = row.getAttribute('data-submitted') || '—';
    const reviewed = row.getAttribute('data-reviewed') || '—';

    document.getElementById('detailTrainerName').textContent = name;
    document.getElementById('detailTrainerEmail').textContent = email;
    document.getElementById('detailNotes').textContent = notes;
    document.getElementById('detailAdminNotes').textContent = adminNotes;
    document.getElementById('detailSubmitted').textContent = submitted;
    document.getElementById('detailReviewed').textContent = reviewed;

    const badge = document.getElementById('detailStatus');
    badge.textContent = status.replace('_', ' ');
    badge.className = 'inline-flex items-center px-2.5 py-0.5 rounded-full text-xs font-medium';
    if (status === 'APPROVED') {
        badge.classList.add('bg-green-100', 'text-green-800');
    } else if (status === 'REJECTED') {
        badge.classList.add('bg-red-100', 'text-red-800');
    } else if (status === 'NEEDS_INFO') {
        badge.classList.add('bg-orange-100', 'text-orange-800');
    } else {
        badge.classList.add('bg-yellow-100', 'text-yellow-800');
    }

    document.getElementById('detailEmpty').classList.add('hidden');
    document.getElementById('detailPanel').classList.remove('hidden');
}

function approveRequest(button) {
    const requestId = button.getAttribute('data-request-id');
    document.getElementById('approveForm').action = '/super-admin/verification/' + encodeURIComponent(requestId) + '/approve';
    showModal('approveModal');
}

function rejectRequest(button) {
    const requestId = button.getAttribute('data-request-id');
    document.getElementById('rejectForm').action = '/super-admin/verification/' + encodeURIComponent(requestId) + '/reject';
    showModal('rejectModal');
}

function requestMoreInfo(button) {
    const requestId = button.getAttribute('data-request-id');
    document.getElementById('requestInfoForm').action = '/super-admin/verification/' + encodeURIComponent(requestId) + '/request-info';
    showModal('requestInfoModal');
}

function showModal(modalId) {
    document.getElementById(modalId).classList.remove('hidden');
    document.getElementById(modalId).classList.add('flex');
}

function hideModal(modalId) {
    document.getElementById(modalId).classList.add('hidden');
    document.getElementById(modalId).classList.remove('flex');
}

// Close modals on background click
['approveModal', 'rejectModal', 'requestInfoModal'].forEach(modalId => {
    document.getElementById(modalId).addEventListener('click', function(e) {
        if (e.target === this) {
            hideModal(modalId);
        }
    });
});

// Close modals on Escape key
document.addEventListener('keydown', function(e) {
    if (e.key === 'Escape') {
        hideModal('approveModal');
        hideModal('rejectModal');
        hideModal('requestInfoModal');
    }
});
