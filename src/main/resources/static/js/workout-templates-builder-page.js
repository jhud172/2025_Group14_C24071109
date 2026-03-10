(function () {
    // ── State ─────────────────────────────────────────────────────
    const initialConfigJson = (typeof builderInitialConfigJson !== 'undefined') ? builderInitialConfigJson : null;
    let canvasComponents = [];

    // ── Elements ──────────────────────────────────────────────────
    const palette = document.getElementById('palette');
    const dropZone = document.getElementById('canvas-drop-zone');
    const emptyMsg = document.getElementById('canvas-empty');
    const saveBtn = document.getElementById('save-template-btn');
    const form = document.getElementById('template-save-form');

    // ── Load existing configJson if editing ───────────────────────
    if (initialConfigJson) {
        try {
            const cfg = JSON.parse(initialConfigJson);
            if (cfg.components && Array.isArray(cfg.components)) {
                cfg.components.forEach(c => addComponent(c));
            }
            if (cfg.theme) setSelectValue('prop-theme', cfg.theme);
            if (cfg.transition) setSelectValue('prop-transition', cfg.transition);
            if (cfg.density) setSelectValue('prop-density', cfg.density);
            if (cfg.progress !== undefined) document.getElementById('prop-progress').checked = cfg.progress;
            if (cfg.restTimer !== undefined) document.getElementById('prop-rest-timer').checked = cfg.restTimer;
        } catch (e) { console.warn('Failed to parse template configJson:', e); }
    }

    // ── Drag from palette ─────────────────────────────────────────
    let draggingComponent = null;
    palette.querySelectorAll('.palette-item').forEach(item => {
        item.addEventListener('dragstart', e => {
            draggingComponent = item.getAttribute('data-component');
            e.dataTransfer.effectAllowed = 'copy';
        });
        item.addEventListener('dragend', () => { draggingComponent = null; });
    });

    dropZone.addEventListener('dragover', e => {
        e.preventDefault();
        e.dataTransfer.dropEffect = 'copy';
        dropZone.classList.add('drag-over');
    });
    dropZone.addEventListener('dragleave', () => dropZone.classList.remove('drag-over'));
    dropZone.addEventListener('drop', e => {
        e.preventDefault();
        dropZone.classList.remove('drag-over');
        if (draggingComponent) {
            addComponent(draggingComponent);
        }
    });

    function addComponent(name) {
        canvasComponents.push(name);
        renderCanvas();
    }

    function renderCanvas() {
        // Remove old canvas items
        dropZone.querySelectorAll('.canvas-item').forEach(el => el.remove());
        if (canvasComponents.length === 0) {
            emptyMsg.style.display = '';
        } else {
            emptyMsg.style.display = 'none';
            canvasComponents.forEach((comp, idx) => {
                const div = document.createElement('div');
                div.className = 'canvas-item';
                div.innerHTML = '<span class="flex-1">' + escapeHtml(comp) + '</span>' +
                    '<button type="button" class="remove-btn" aria-label="Remove ' + escapeHtml(comp) + '" data-index="' + idx + '">✕</button>';
                div.querySelector('.remove-btn').addEventListener('click', function () {
                    canvasComponents.splice(parseInt(this.getAttribute('data-index')), 1);
                    renderCanvas();
                });
                dropZone.appendChild(div);
            });
        }
    }

    function escapeHtml(str) {
        return String(str).replace(/&/g, '&amp;').replace(/</g, '&lt;').replace(/>/g, '&gt;');
    }

    function setSelectValue(id, value) {
        const el = document.getElementById(id);
        if (el) el.value = value;
    }

    // ── Save ──────────────────────────────────────────────────────
    saveBtn.addEventListener('click', function () {
        const name = document.getElementById('prop-name').value.trim();
        if (!name) {
            alert('Please enter a template name.');
            document.getElementById('prop-name').focus();
            return;
        }
        const layoutType = document.getElementById('prop-layout').value;
        const configJson = JSON.stringify({
            layout: layoutType.toLowerCase(),
            theme: document.getElementById('prop-theme').value,
            transition: document.getElementById('prop-transition').value,
            density: document.getElementById('prop-density').value,
            progress: document.getElementById('prop-progress').checked,
            restTimer: document.getElementById('prop-rest-timer').checked,
            components: canvasComponents
        });

        document.getElementById('form-name').value = name;
        document.getElementById('form-layout-type').value = layoutType;
        document.getElementById('form-config-json').value = configJson;
        form.submit();
    });

})();
