(function () {
  const openBtn = document.getElementById('open-dashboard-customise');
  const modal = document.getElementById('dashboard-customise');
  const closeBtn = document.getElementById('close-dashboard-customise');
  const cancelBtn = document.getElementById('cancel-dashboard-customise');
  const saveBtn = document.getElementById('save-dashboard-customise');
  const list = document.getElementById('dashboard-module-list');

  if (!openBtn || !modal || !list || !saveBtn) {
    return;
  }

  const open = () => {
    modal.classList.remove('hidden');
    modal.setAttribute('aria-hidden', 'false');
  };

  const close = () => {
    modal.classList.add('hidden');
    modal.setAttribute('aria-hidden', 'true');
  };

  openBtn.addEventListener('click', open);
  closeBtn && closeBtn.addEventListener('click', close);
  cancelBtn && cancelBtn.addEventListener('click', close);
  modal.addEventListener('click', (e) => {
    if (e.target && e.target.classList.contains('dashboard-modal__backdrop')) {
      close();
    }
  });

  // Drag & drop
  let dragging = null;

  list.querySelectorAll('.dashboard-item').forEach((item) => {
    item.addEventListener('dragstart', () => {
      dragging = item;
      item.classList.add('is-dragging');
      item.classList.add('opacity-60');
    });

    item.addEventListener('dragend', () => {
      dragging = null;
      item.classList.remove('is-dragging');
      item.classList.remove('opacity-60');
    });

    item.addEventListener('dragover', (e) => {
      e.preventDefault();
      if (!dragging || dragging === item) return;

      const rect = item.getBoundingClientRect();
      const before = e.clientY < rect.top + rect.height / 2;
      const parent = item.parentNode;
      if (!parent) return;

      if (before) {
        parent.insertBefore(dragging, item);
      } else {
        parent.insertBefore(dragging, item.nextSibling);
      }
    });
  });

  const getCsrf = () => {
    const header = document.getElementById('csrf-header');
    const token = document.getElementById('csrf-token');
    return {
      headerName: header ? header.value : null,
      token: token ? token.value : null,
    };
  };

  saveBtn.addEventListener('click', async () => {
    const items = Array.from(list.querySelectorAll('.dashboard-item')).map((li, index) => {
      const moduleKey = li.getAttribute('data-module-key');
      const checkbox = li.querySelector('input[type="checkbox"]');
      return {
        moduleKey,
        sortIndex: index,
        enabled: checkbox ? checkbox.checked : true,
      };
    });

    const csrf = getCsrf();
    const headers = { 'Content-Type': 'application/json' };
    if (csrf.headerName && csrf.token) {
      headers[csrf.headerName] = csrf.token;
    }

    const resp = await fetch('/profile/layout', {
      method: 'POST',
      headers,
      body: JSON.stringify({ items }),
    });

    if (resp.ok) {
      window.location.reload();
      return;
    }

    // If it fails, keep modal open so user doesn't lose their work.
    alert('Could not save dashboard layout. Please try again.');
  });
})();
