/* Profile edit mode */
const toggleButton = document.getElementById('toggle-profile-edit');
const cancelButton = document.getElementById('cancel-profile-edit');
const profileShell = document.getElementById('profile-shell');
const editPanels   = document.querySelectorAll('.profile-edit');
const editForm     = document.getElementById('profile-edit-form');
const avatarButton = document.querySelector('[data-avatar-button]');
const profileImageInput = document.getElementById('profileImageInput');
const profileImageError = document.getElementById('profile-image-error');
const profileImageDropzone = document.getElementById('profile-image-dropzone');
const accessibilityForm     = document.getElementById('accessibility-form');
const accessibilityFeedback = document.getElementById('accessibility-feedback');

let isDirty = false;
let isSubmittingProfile = false;

const setEditing = (editing) => {
    if (!profileShell || !editPanels.length) return;
    profileShell.setAttribute('data-edit-mode', editing ? 'true' : 'false');
    editPanels.forEach((panel) => panel.classList.toggle('hidden', !editing));
    if (toggleButton) {
        const openLabel  = toggleButton.getAttribute('data-edit-open-label')  || 'Edit Profile';
        const closeLabel = toggleButton.getAttribute('data-edit-close-label') || 'Close editor';
        const span = toggleButton.querySelector('span');
        if (span) span.textContent = editing ? closeLabel : openLabel;
    }
};

if (toggleButton && profileShell && editPanels.length) {
    toggleButton.addEventListener('click', () => {
        const currentlyEditing = profileShell.getAttribute('data-edit-mode') === 'true';
        if (currentlyEditing && isDirty && !confirm('Discard unsaved changes?')) return;
        if (!currentlyEditing) isDirty = false;
        setEditing(!currentlyEditing);
    });
}

if (cancelButton && editForm) {
    cancelButton.addEventListener('click', () => {
        if (isDirty && !confirm('Discard unsaved changes?')) return;
        editForm.reset();
        isDirty = false;
        setEditing(false);
    });
}

if (editForm) {
    editForm.addEventListener('change', () => { isDirty = true; });
    editForm.addEventListener('submit', () => {
        isSubmittingProfile = true;
        isDirty = false;
    });
}

window.addEventListener('beforeunload', (event) => {
    if (!isSubmittingProfile && profileShell && profileShell.getAttribute('data-edit-mode') === 'true' && isDirty) {
        event.preventDefault();
        event.returnValue = '';
    }
});

document.addEventListener('click', (event) => {
    const link = event.target.closest('a');
    if (!link) return;
    if (profileShell && profileShell.getAttribute('data-edit-mode') === 'true' && isDirty) {
        if (!confirm('You have unsaved changes. Leave without saving?')) event.preventDefault();
    }
});

if (avatarButton && profileImageInput) {
    avatarButton.addEventListener('click', () => {
        if (profileShell && profileShell.getAttribute('data-edit-mode') === 'true') {
            profileImageInput.click();
        }
    });
}

if (profileImageInput && profileImageError) {
    const validateImageFile = (file) => {
        const maxSize = 2 * 1024 * 1024;
        const allowedTypes = ['image/png', 'image/jpeg', 'image/webp'];
        if (!file) return true;
        if (!allowedTypes.includes(file.type) || file.size > maxSize) {
            profileImageInput.value = '';
            profileImageError.textContent = 'Please choose a PNG, JPG, or WebP file under 2MB.';
            profileImageError.classList.remove('hidden');
            return false;
        }
        return true;
    };

    profileImageInput.addEventListener('change', () => {
        profileImageError.classList.add('hidden');
        const file = profileImageInput.files && profileImageInput.files[0];
        validateImageFile(file);
    });

    if (profileImageDropzone) {
        ['dragenter', 'dragover'].forEach((eventName) => {
            profileImageDropzone.addEventListener(eventName, (event) => {
                event.preventDefault();
                event.stopPropagation();
                if (profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') return;
                profileImageDropzone.classList.add('is-drag-over');
            });
        });

        ['dragleave', 'drop'].forEach((eventName) => {
            profileImageDropzone.addEventListener(eventName, (event) => {
                event.preventDefault();
                event.stopPropagation();
                profileImageDropzone.classList.remove('is-drag-over');
            });
        });

        profileImageDropzone.addEventListener('drop', (event) => {
            if (profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') return;
            const files = event.dataTransfer && event.dataTransfer.files;
            if (!files || !files.length) return;
            const file = files[0];
            if (!validateImageFile(file)) return;

            // Use DataTransfer to assign dropped file to the file input.
            const transfer = new DataTransfer();
            transfer.items.add(file);
            profileImageInput.files = transfer.files;
            isDirty = true;
        });
    }
}

if (accessibilityForm && accessibilityFeedback) {
    accessibilityForm.addEventListener('change', () => {
        accessibilityFeedback.classList.remove('hidden');
        clearTimeout(accessibilityFeedback._timeout);
        accessibilityFeedback._timeout = setTimeout(() => accessibilityFeedback.classList.add('hidden'), 3000);
        const colorBlind = accessibilityForm.querySelector('input[name="colorBlindMode"]');
        if (colorBlind) {
            document.body.dataset.colorBlind = colorBlind.checked ? 'on' : 'off';
            document.body.classList.toggle('color-blind', colorBlind.checked);
        }
    });
}

/* Premium profile customiser live preview */
(function () {
    const customiserForm = document.getElementById('profile-customiser-form');
    if (!customiserForm) return;

    const bannerSelect = document.getElementById('customiser-banner');
    const ringSelect = document.getElementById('customiser-ring');
    const backSelect = document.getElementById('customiser-back');
    const previewBanner = document.getElementById('profile-preview-banner');
    const previewRing = document.getElementById('profile-preview-ring');
    const previewBack = document.getElementById('profile-preview-card-back');
    const sidebarBanner = document.getElementById('profile-sidebar-banner-pill');
    const sidebarRing = document.getElementById('profile-sidebar-avatar-ring');
    const milestoneChecks = Array.from(customiserForm.querySelectorAll('.customiser-milestone'));
    const milestoneMsg = document.getElementById('customiser-milestone-msg');

    function replaceTokenClass(el, prefix, value) {
        if (!el) return;
        Array.from(el.classList)
            .filter((cls) => cls.indexOf(prefix) === 0)
            .forEach((cls) => el.classList.remove(cls));
        if (value) {
            el.classList.add(prefix + value);
        }
    }

    function currentBannerKey() {
        return (bannerSelect && bannerSelect.value ? bannerSelect.value : 'AURORA').toLowerCase();
    }

    function currentRingKey() {
        return (ringSelect && ringSelect.value ? ringSelect.value : 'NEON_DUAL').toLowerCase();
    }

    function currentBackKey() {
        return (backSelect && backSelect.value ? backSelect.value : 'GLASS').toLowerCase();
    }

    function applyPreview() {
        const bannerKey = currentBannerKey();
        const ringKey = currentRingKey();
        const backKey = currentBackKey();

        replaceTokenClass(previewBanner, 'profile-banner-pill--', bannerKey);
        previewBanner.classList.add('profile-banner-pill');
        if (bannerSelect && previewBanner) {
            previewBanner.textContent = bannerSelect.value + ' banner';
        }

        replaceTokenClass(previewRing, 'profile-avatar-ring--', ringKey);
        previewRing.classList.add('profile-avatar-ring');

        replaceTokenClass(previewBack, 'profile-preview-card-back--', backKey);

        replaceTokenClass(sidebarBanner, 'profile-banner-pill--', bannerKey);
        if (sidebarBanner && bannerSelect) {
            sidebarBanner.classList.add('profile-banner-pill');
            sidebarBanner.textContent = bannerSelect.value + ' banner';
        }

        replaceTokenClass(sidebarRing, 'profile-avatar-ring--', ringKey);
        if (sidebarRing) {
            sidebarRing.classList.add('profile-avatar-ring');
        }
    }

    function enforceMilestoneCap(changedInput) {
        const checked = milestoneChecks.filter((input) => input.checked);
        if (checked.length <= 6) {
            if (milestoneMsg) milestoneMsg.classList.add('hidden');
            return;
        }
        if (changedInput) {
            changedInput.checked = false;
        }
        if (milestoneMsg) milestoneMsg.classList.remove('hidden');
    }

    [bannerSelect, ringSelect, backSelect].forEach((el) => {
        if (!el) return;
        el.addEventListener('change', applyPreview);
    });

    milestoneChecks.forEach((input) => {
        input.addEventListener('change', () => enforceMilestoneCap(input));
    });

    applyPreview();
})();

/* Settings Drawer */
const settingsDrawerRoot = document.getElementById('settings-drawer-root');
const settingsOverlay    = document.getElementById('settings-overlay');
const openSettingsBtn    = document.getElementById('open-settings-drawer');
const openSettingsFromActivityBtn = document.getElementById('open-settings-drawer-from-activity');
const closeSettingsBtn   = document.getElementById('close-settings-drawer');

function openSettingsDrawer() {
    if (!settingsDrawerRoot) return;
    settingsDrawerRoot.classList.add('is-open');
    settingsDrawerRoot.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    if (closeSettingsBtn) closeSettingsBtn.focus();
}
window.openSettingsDrawer = openSettingsDrawer;

function closeSettingsDrawer() {
    if (!settingsDrawerRoot) return;
    settingsDrawerRoot.classList.remove('is-open');
    settingsDrawerRoot.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
    if (openSettingsBtn) openSettingsBtn.focus();
}

if (openSettingsBtn)  openSettingsBtn.addEventListener('click', openSettingsDrawer);
if (openSettingsFromActivityBtn) openSettingsFromActivityBtn.addEventListener('click', openSettingsDrawer);
if (closeSettingsBtn) closeSettingsBtn.addEventListener('click', closeSettingsDrawer);
if (settingsOverlay)  settingsOverlay.addEventListener('click', closeSettingsDrawer);

/* Purchases Drawer */
const purchasesDrawerRoot = document.getElementById('purchases-drawer-root');
const purchasesOverlay    = document.getElementById('purchases-overlay');
const openPurchasesBtn    = document.getElementById('open-purchases-drawer');
const openPurchasesFromActivityBtn = document.getElementById('open-purchases-drawer-from-activity');
const closePurchasesBtn   = document.getElementById('close-purchases-drawer');

function openPurchasesDrawer() {
    if (!purchasesDrawerRoot) return;
    purchasesDrawerRoot.classList.add('is-open');
    purchasesDrawerRoot.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    if (closePurchasesBtn) closePurchasesBtn.focus();
}

function closePurchasesDrawer() {
    if (!purchasesDrawerRoot) return;
    purchasesDrawerRoot.classList.remove('is-open');
    purchasesDrawerRoot.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
    if (openPurchasesBtn) openPurchasesBtn.focus();
}

if (openPurchasesBtn)  openPurchasesBtn.addEventListener('click', openPurchasesDrawer);
if (openPurchasesFromActivityBtn) openPurchasesFromActivityBtn.addEventListener('click', openPurchasesDrawer);
if (closePurchasesBtn) closePurchasesBtn.addEventListener('click', closePurchasesDrawer);
if (purchasesOverlay)  purchasesOverlay.addEventListener('click', closePurchasesDrawer);

document.addEventListener('keydown', (e) => {
    if (e.key === 'Escape') {
        if (purchasesDrawerRoot && purchasesDrawerRoot.classList.contains('is-open')) {
            closePurchasesDrawer();
        } else if (settingsDrawerRoot && settingsDrawerRoot.classList.contains('is-open')) {
            closeSettingsDrawer();
        }
    }
});

/* Purchase Group Collapsible */
document.addEventListener('click', (e) => {
    const toggle = e.target.closest('.purchase-group-toggle');
    if (!toggle) return;
    
    const group = toggle.closest('.border.border-slate-200\\/70');
    if (!group) return;
    
    const content = group.querySelector('.purchase-group-content');
    const icon = toggle.querySelector('.purchase-group-icon');
    
    if (content) {
        content.classList.toggle('hidden');
    }
    if (icon) {
        icon.style.transform = content.classList.contains('hidden') ? '' : 'rotate(180deg)';
    }
});


/* Theme Picker */
(function () {
    const picker   = document.getElementById('theme-picker');
    const feedback = document.getElementById('theme-save-feedback');
    const csrfInput = document.querySelector('#theme-form input[name="_csrf"]') ||
                      document.querySelector('input[name="_csrf"]');

    if (!picker) return;

    function applyTheme(theme) {
        if (theme === 'DARK') {
            document.documentElement.classList.add('dark');
            document.documentElement.setAttribute('data-theme', 'dark');
        } else if (theme === 'LIGHT') {
            document.documentElement.classList.remove('dark');
            document.documentElement.setAttribute('data-theme', 'light');
        } else {
            /* SYSTEM: match OS preference */
            const prefersDark = window.matchMedia('(prefers-color-scheme: dark)').matches;
            document.documentElement.classList.toggle('dark', prefersDark);
            document.documentElement.setAttribute('data-theme', 'system');
        }
    }

    picker.addEventListener('click', function (e) {
        const btn = e.target.closest('.profile-theme-option');
        if (!btn) return;

        const theme = btn.getAttribute('data-theme');
        if (!theme) return;

        /* Update button pressed states */
        picker.querySelectorAll('.profile-theme-option').forEach(function (b) {
            b.setAttribute('aria-pressed', b === btn ? 'true' : 'false');
        });

        /* Apply the theme immediately */
        applyTheme(theme);

        /* Persist via fetch POST */
        const csrfToken  = csrfInput ? csrfInput.value : null;
        const csrfName   = csrfInput ? csrfInput.name  : '_csrf';
        const body = new URLSearchParams({ theme: theme });
        if (csrfToken) body.append(csrfName, csrfToken);

        fetch('/profile/settings/theme', {
            method: 'POST',
            headers: { 'Content-Type': 'application/x-www-form-urlencoded' },
            body: body.toString()
        }).then(function (res) {
            if (res.ok || res.redirected) {
                if (feedback) {
                    feedback.textContent = 'Appearance saved.';
                    feedback.classList.remove('hidden', 'text-rose-600', 'dark:text-rose-400');
                    feedback.classList.add('text-emerald-600', 'dark:text-emerald-400');
                    clearTimeout(feedback._t);
                    feedback._t = setTimeout(function () {
                        feedback.classList.add('hidden');
                    }, 2500);
                }
            } else {
                if (feedback) {
                    feedback.textContent = 'Could not save preference.';
                    feedback.classList.remove('hidden', 'text-emerald-600', 'dark:text-emerald-400');
                    feedback.classList.add('text-rose-600', 'dark:text-rose-400');
                    clearTimeout(feedback._t);
                    feedback._t = setTimeout(function () {
                        feedback.classList.add('hidden');
                    }, 3000);
                }
            }
        }).catch(function () {
            if (feedback) {
                feedback.textContent = 'Could not save preference.';
                feedback.classList.remove('hidden', 'text-emerald-600', 'dark:text-emerald-400');
                feedback.classList.add('text-rose-600', 'dark:text-rose-400');
                clearTimeout(feedback._t);
                feedback._t = setTimeout(function () {
                    feedback.classList.add('hidden');
                }, 3000);
            }
        });
    });
})();
