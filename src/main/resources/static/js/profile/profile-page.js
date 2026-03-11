/* Profile edit mode */
const toggleButton = document.getElementById('toggle-profile-edit');
const cancelButton = document.getElementById('cancel-profile-edit');
const saveProfileButton = document.getElementById('save-profile-edit');
const profileShell = document.getElementById('profile-shell');
const editPanels   = document.querySelectorAll('.profile-edit');
const editForm     = document.getElementById('profile-edit-form');

const profileImageInput = document.getElementById('profileImageInput');
const profileImageError = document.getElementById('profile-image-error');
const profileImageDropzone = document.getElementById('profile-image-dropzone');
const profileImageSelectButton = document.getElementById('profile-image-select');
const profileImageRemoveButton = document.getElementById('profile-image-remove');
const profileImageRevertButton = document.getElementById('profile-image-revert');
const profileImageFileName = document.getElementById('profile-image-file-name');
const removeProfileImageInput = document.getElementById('remove-profile-image');
const profileUploadThumbOpen = document.getElementById('profile-upload-thumb-open');
const profileUploadPreviewImage = document.getElementById('profile-upload-preview-image');
const profileUploadPreviewFallback = document.getElementById('profile-upload-preview-fallback');
const profileSidebarAvatarImage = document.getElementById('profile-sidebar-avatar-image');
const profileSidebarAvatarFallback = document.getElementById('profile-sidebar-avatar-fallback');
const profileFirstNameInput = document.getElementById('profile-first-name-input');
const profileLastNameInput = document.getElementById('profile-last-name-input');
const profileDateOfBirthInput = document.getElementById('profile-date-of-birth-input');
const usernameInput = document.getElementById('profile-username-input');
const usernameStatus = document.getElementById('profile-username-status');
const accessibilityForm     = document.getElementById('accessibility-form');
const accessibilityFeedback = document.getElementById('accessibility-feedback');
const unsavedModal = document.getElementById('profile-unsaved-modal');
const unsavedSaveBtn = document.getElementById('profile-unsaved-save');
const unsavedDiscardBtn = document.getElementById('profile-unsaved-discard');
const unsavedCancelBtn = document.getElementById('profile-unsaved-cancel');

let isDirty = false;
let isSubmittingProfile = false;
let usernameState = 'idle';
let usernameCheckTimer = null;
let usernameCheckController = null;

function setUsernameStatus(message, state) {
    if (!usernameStatus) return;
    usernameState = state || 'idle';
    usernameStatus.textContent = message || '';
    usernameStatus.classList.remove('is-idle', 'is-checking', 'is-ok', 'is-error');
    usernameStatus.classList.add('is-' + usernameState);
}

function normalizeUsernameInputValue(value) {
    return String(value || '')
        .replace(/@/g, '')
        .replace(/\s+/g, '')
        .toLowerCase();
}

function syncCustomiserIntoProfileForm(customiserForm, profileForm) {
    if (!customiserForm || !profileForm) return;

    const allowedCustomiserKeys = new Set([
        'bannerTheme',
        'ringStyle',
        'cardBackStyle',
        'textColor',
        'generalTextColor',
        'milestoneKeys'
    ]);

    profileForm
        .querySelectorAll('input[data-customiser-shadow="true"]')
        .forEach((input) => input.remove());

    const formData = new FormData(customiserForm);
    formData.forEach((value, key) => {
        if (!allowedCustomiserKeys.has(key)) {
            return;
        }
        const hidden = document.createElement('input');
        hidden.type = 'hidden';
        hidden.name = key;
        hidden.value = String(value);
        hidden.setAttribute('data-customiser-shadow', 'true');
        profileForm.appendChild(hidden);
    });
}

function ensureCsrfTokenOnForm(form) {
    if (!form) return false;

    const existing = form.querySelector('input[name="_csrf"]');
    if (existing && existing.value && existing.value.trim()) {
        return true;
    }

    const source = document.querySelector('#verify-email-inline-form input[name="_csrf"], #verify-phone-inline-form input[name="_csrf"], input[name="_csrf"]');
    if (source && source.value && source.value.trim()) {
        if (existing) {
            existing.value = source.value;
        } else {
            const hidden = document.createElement('input');
            hidden.type = 'hidden';
            hidden.name = '_csrf';
            hidden.value = source.value;
            form.appendChild(hidden);
        }
        return true;
    }

    return false;
}

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

function openUnsavedModal() {
    if (!unsavedModal) return;
    unsavedModal.classList.remove('hidden');
    unsavedModal.setAttribute('aria-hidden', 'false');
    if (unsavedSaveBtn) unsavedSaveBtn.focus();
}

function closeUnsavedModal() {
    if (!unsavedModal) return;
    unsavedModal.classList.add('hidden');
    unsavedModal.setAttribute('aria-hidden', 'true');
}

if (toggleButton && profileShell && editPanels.length) {
    toggleButton.addEventListener('click', () => {
        const currentlyEditing = profileShell.getAttribute('data-edit-mode') === 'true';
        if (currentlyEditing && isDirty) {
            openUnsavedModal();
            return;
        }
        if (!currentlyEditing) isDirty = false;
        setEditing(!currentlyEditing);
    });
}

if (unsavedSaveBtn && editForm) {
    unsavedSaveBtn.addEventListener('click', () => {
        closeUnsavedModal();
        editForm.requestSubmit();
    });
}

if (unsavedDiscardBtn && editForm) {
    unsavedDiscardBtn.addEventListener('click', () => {
        closeUnsavedModal();
        editForm.reset();
        isDirty = false;
        setEditing(false);
        setUsernameStatus('', 'idle');
    });
}

if (unsavedCancelBtn) {
    unsavedCancelBtn.addEventListener('click', () => {
        closeUnsavedModal();
        if (toggleButton) toggleButton.focus();
    });
}

if (unsavedModal) {
    unsavedModal.addEventListener('click', (event) => {
        if (event.target === unsavedModal || event.target === unsavedModal.firstElementChild) {
            closeUnsavedModal();
        }
    });

    document.addEventListener('keydown', (event) => {
        if (event.key === 'Escape' && !unsavedModal.classList.contains('hidden')) {
            closeUnsavedModal();
        }
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
    editForm.addEventListener('submit', async (event) => {
        if (!ensureCsrfTokenOnForm(editForm)) {
            event.preventDefault();
            alert('Security token missing. Please refresh the page and try saving again.');
            return;
        }

        if (profileDateOfBirthInput && !profileDateOfBirthInput.disabled) {
            const originalDob = String(profileDateOfBirthInput.getAttribute('data-original-value') || '').trim();
            const pendingDob = String(profileDateOfBirthInput.value || '').trim();
            if (!originalDob && pendingDob) {
                event.preventDefault();
                showDOBAwarenessModal();
                return;
            }
        }

        if (usernameState === 'checking') {
            event.preventDefault();
            setUsernameStatus('Please wait for username check to finish.', 'error');
            return;
        }
        if (usernameState === 'error') {
            event.preventDefault();
            if (usernameInput) usernameInput.focus();
            return;
        }

        const customiserForm = document.getElementById('profile-customiser-form');
        const isEditing = profileShell && profileShell.getAttribute('data-edit-mode') === 'true';

        if (isEditing && customiserForm) {
            syncCustomiserIntoProfileForm(customiserForm, editForm);
        }

        isSubmittingProfile = true;
        isDirty = false;
    });
}

if (usernameInput && usernameStatus) {
    const currentUsername = (usernameInput.getAttribute('data-current-username') || '').trim().toLowerCase();

    const checkUsername = () => {
        const rawValue = usernameInput.value || '';
        const candidate = normalizeUsernameInputValue(rawValue);

        if (!candidate) {
            setUsernameStatus('Username is required.', 'error');
            return;
        }
        if (candidate.length < 3 || candidate.length > 100) {
            setUsernameStatus('Username must be between 3 and 100 characters.', 'error');
            return;
        }
        if (candidate === currentUsername) {
            setUsernameStatus('This is your current username.', 'ok');
            return;
        }

        if (usernameCheckController) {
            usernameCheckController.abort();
        }
        usernameCheckController = new AbortController();
        setUsernameStatus('Checking username availability...', 'checking');

        fetch('/profile/username-availability?username=' + encodeURIComponent(candidate), {
            method: 'GET',
            credentials: 'same-origin',
            signal: usernameCheckController.signal,
            headers: {
                'Accept': 'application/json'
            }
        })
            .then((response) => response.ok ? response.json() : Promise.reject(new Error('Username check failed.')))
            .then((data) => {
                if (data && data.available) {
                    setUsernameStatus(data.message || 'Username is available.', 'ok');
                    return;
                }
                setUsernameStatus((data && data.message) || 'That username is already taken.', 'error');
            })
            .catch((error) => {
                if (error && error.name === 'AbortError') {
                    return;
                }
                setUsernameStatus('Could not check username right now.', 'error');
            });
    };

    const scheduleCheck = () => {
        if (usernameCheckTimer) {
            clearTimeout(usernameCheckTimer);
        }
        usernameCheckTimer = setTimeout(checkUsername, 260);
    };

    usernameInput.addEventListener('input', () => {
        const normalized = normalizeUsernameInputValue(usernameInput.value);
        if (usernameInput.value !== normalized) {
            usernameInput.value = normalized;
        }
        isDirty = true;
        scheduleCheck();
    });

    usernameInput.addEventListener('keydown', (event) => {
        if ((event.key === '@' || event.key === ' ') && !event.ctrlKey && !event.metaKey) {
            event.preventDefault();
        }
    });

    usernameInput.value = normalizeUsernameInputValue(usernameInput.value);

    usernameInput.addEventListener('blur', checkUsername);
    setUsernameStatus('', 'idle');
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

if (profileImageInput && profileImageError) {
    const originalImageSrc =
        (profileUploadPreviewImage && profileUploadPreviewImage.getAttribute('data-original-src'))
        || (profileSidebarAvatarImage && profileSidebarAvatarImage.getAttribute('data-original-src'))
        || null;
    let currentPreviewSrc = originalImageSrc;
    let objectPreviewUrl = null;

    const releaseObjectPreview = () => {
        if (objectPreviewUrl) {
            URL.revokeObjectURL(objectPreviewUrl);
            objectPreviewUrl = null;
        }
    };

    const syncFileName = (file) => {
        if (!profileImageFileName) return;
        profileImageFileName.textContent = file ? file.name : 'No file selected';
    };

    const applyPreviewSource = (sourceUrl) => {
        currentPreviewSrc = sourceUrl || null;

        if (profileUploadPreviewImage) {
            if (sourceUrl) {
                profileUploadPreviewImage.src = sourceUrl;
                profileUploadPreviewImage.classList.remove('hidden');
            } else {
                profileUploadPreviewImage.removeAttribute('src');
                profileUploadPreviewImage.classList.add('hidden');
            }
        }

        if (profileSidebarAvatarImage) {
            if (sourceUrl) {
                profileSidebarAvatarImage.src = sourceUrl;
                profileSidebarAvatarImage.classList.remove('hidden');
            } else {
                profileSidebarAvatarImage.removeAttribute('src');
                profileSidebarAvatarImage.classList.add('hidden');
            }
        }

        if (profileUploadPreviewFallback) {
            profileUploadPreviewFallback.classList.toggle('hidden', Boolean(sourceUrl));
        }
        if (profileSidebarAvatarFallback) {
            profileSidebarAvatarFallback.classList.toggle('hidden', Boolean(sourceUrl));
        }
    };

    const openPreviewImage = () => {
        if (!currentPreviewSrc) return;
        window.open(currentPreviewSrc, '_blank', 'noopener');
    };

    const resetImageSelection = () => {
        releaseObjectPreview();
        profileImageInput.value = '';
        if (removeProfileImageInput) {
            removeProfileImageInput.value = 'false';
        }
        syncFileName(null);
        applyPreviewSource(originalImageSrc);
        profileImageError.classList.add('hidden');
    };

    const markRemoveImage = () => {
        releaseObjectPreview();
        profileImageInput.value = '';
        if (removeProfileImageInput) {
            removeProfileImageInput.value = 'true';
        }
        syncFileName(null);
        applyPreviewSource(null);
        profileImageError.classList.add('hidden');
        isDirty = true;
    };

    if (profileImageSelectButton) {
        profileImageSelectButton.addEventListener('click', () => {
            if (profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') return;
            profileImageInput.click();
        });
    }

    if (profileImageRevertButton) {
        profileImageRevertButton.addEventListener('click', () => {
            if (profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') return;
            resetImageSelection();
            isDirty = true;
        });
    }

    if (profileImageRemoveButton) {
        profileImageRemoveButton.addEventListener('click', () => {
            if (profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') return;
            markRemoveImage();
        });
    }

    if (profileUploadThumbOpen) {
        profileUploadThumbOpen.addEventListener('click', openPreviewImage);
    }

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
        if (!validateImageFile(file)) {
            if (removeProfileImageInput) {
                removeProfileImageInput.value = 'false';
            }
            syncFileName(null);
            applyPreviewSource(originalImageSrc);
            return;
        }
        if (!file) {
            if (removeProfileImageInput) {
                removeProfileImageInput.value = 'false';
            }
            syncFileName(null);
            applyPreviewSource(originalImageSrc);
            return;
        }
        if (removeProfileImageInput) {
            removeProfileImageInput.value = 'false';
        }
        releaseObjectPreview();
        objectPreviewUrl = URL.createObjectURL(file);
        syncFileName(file);
        applyPreviewSource(objectPreviewUrl);
        isDirty = true;
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
            profileImageInput.dispatchEvent(new Event('change', { bubbles: true }));
            isDirty = true;
        });
    }

    if (!profileUploadPreviewImage && !profileSidebarAvatarImage) {
        syncFileName(null);
    } else {
        applyPreviewSource(originalImageSrc);
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

(function () {
    const completionWidget = document.getElementById('profile-completion-widget');
    const completionTooltip = document.getElementById('profile-completion-tooltip');
    const completionPercent = document.getElementById('profile-completion-percent');
    const completionBar = document.getElementById('profile-completion-progress-bar');
    const completionTitle = document.getElementById('profile-completion-title');
    const completionMessage = document.getElementById('profile-completion-message');
    const completionAction = document.getElementById('profile-completion-action');
    if (!completionWidget || !completionTooltip || !completionTitle || !completionMessage || !completionAction) return;

    let activeCompletionItem = null;
    let completionPinned = false;
    let hoveringWidget = false;
    let hoveringTooltip = false;
    let hideTimer = null;

    const completionRules = [
        {
            key: 'profileImage',
            isMissing: () => {
                const removeFlag = removeProfileImageInput ? removeProfileImageInput.value === 'true' : false;
                const hasUpload = profileImageInput && profileImageInput.files && profileImageInput.files.length > 0;
                const hasCurrentImage = profileSidebarAvatarImage && profileSidebarAvatarImage.getAttribute('src');
                return removeFlag || (!hasUpload && !hasCurrentImage);
            },
            message: 'Add a profile picture to boost trust and visibility.',
            actionLabel: 'Go to profile picture',
            target: '#profile-image-dropzone',
            mode: 'customise'
        },
        {
            key: 'username',
            isMissing: () => !usernameInput || !normalizeUsernameInputValue(usernameInput.value),
            message: 'Choose a username so people can find you easily.',
            actionLabel: 'Go to username',
            target: '#profile-username-input',
            mode: 'customise'
        },
        {
            key: 'email',
            isMissing: () => {
                const emailInput = document.getElementById('profile-email-input');
                const emailVerifiedBadge = document.getElementById('profile-email-verified-badge');
                const emailVerifyButton = document.getElementById('profile-email-verify-btn');
                const hasEmail = Boolean(emailInput && String(emailInput.value || '').trim());
                const isVerified = Boolean(emailVerifiedBadge) || Boolean(emailVerifyButton);
                return !hasEmail || !isVerified;
            },
            message: 'Add and verify your email to secure your account.',
            actionLabel: 'Go to email',
            target: '#profile-email-input',
            mode: 'customise'
        },
        {
            key: 'phone',
            isMissing: () => {
                const phoneInput = document.getElementById('profile-phone-input');
                const phoneVerifiedBadge = document.getElementById('profile-phone-verified-badge');
                const phoneVerifyButton = document.getElementById('profile-phone-verify-btn');
                const hasPhone = Boolean(phoneInput && String(phoneInput.value || '').trim());
                const isVerified = Boolean(phoneVerifiedBadge) || Boolean(phoneVerifyButton);
                return !hasPhone || !isVerified;
            },
            message: 'Add and verify your phone number for account recovery.',
            actionLabel: 'Go to phone number',
            target: '#profile-phone-input',
            mode: 'customise'
        },
        {
            key: 'birthDate',
            isMissing: () => !profileDateOfBirthInput || !String(profileDateOfBirthInput.value || '').trim(),
            message: 'Set your birth date once so your age can be calculated later.',
            actionLabel: 'Set birth date',
            target: '#profile-date-of-birth-input',
            mode: 'customise'
        },
        {
            key: 'name',
            isMissing: () => {
                const firstNameInput = document.getElementById('profile-first-name-input');
                const lastNameInput = document.getElementById('profile-last-name-input');
                const hasFirst = Boolean(firstNameInput && String(firstNameInput.value || '').trim());
                const hasLast = Boolean(lastNameInput && String(lastNameInput.value || '').trim());
                return !hasFirst || !hasLast;
            },
            message: 'Add your first and last name to complete your profile identity.',
            actionLabel: 'Go to name',
            target: '#profile-first-name-input',
            mode: 'customise'
        },
        {
            key: 'bio',
            isMissing: () => {
                const bioInput = document.getElementById('profile-bio-input');
                return !bioInput || !String(bioInput.value || '').trim();
            },
            message: 'Write a short bio so clients know what to expect from you.',
            actionLabel: 'Go to bio',
            target: '#profile-bio-input',
            mode: 'customise'
        },
        {
            key: 'premium',
            isMissing: () => !document.getElementById('profile-sidebar-premium-active'),
            message: 'Upgrade to premium to unlock profile customisation and more.',
            actionLabel: 'View premium plans',
            target: '/pricing'
        }
    ];

    function highlightTarget(target) {
        if (!target) return;
        target.classList.add('profile-focus-highlight');
        setTimeout(() => {
            target.classList.remove('profile-focus-highlight');
        }, 2600);
    }

    function scrollToTarget(targetSelector, mode) {
        if (!targetSelector || targetSelector === '#') return;

        if (targetSelector.charAt(0) !== '#') {
            window.location.href = targetSelector;
            return;
        }

        if (mode === 'customise' && profileShell && profileShell.getAttribute('data-edit-mode') !== 'true') {
            setEditing(true);
        }

        const doScroll = () => {
            const target = document.querySelector(targetSelector);
            if (!target) return;
            target.scrollIntoView({ behavior: 'smooth', block: 'center' });
            if (typeof target.focus === 'function') {
                target.focus({ preventScroll: true });
            }
            highlightTarget(target);
        };

        if (mode === 'options' && typeof openOptionsDrawer === 'function') {
            openOptionsDrawer();
            setTimeout(doScroll, 220);
            return;
        }

        if (mode === 'settings' && typeof openSettingsDrawer === 'function') {
            openSettingsDrawer();
            setTimeout(doScroll, 220);
            return;
        }

        doScroll();
    }

    function computeChecklist() {
        return completionRules.map((rule) => ({
            ...rule,
            missing: rule.isMissing()
        }));
    }

    function clearHideTimer() {
        if (hideTimer) {
            clearTimeout(hideTimer);
            hideTimer = null;
        }
    }

    function scheduleHide() {
        clearHideTimer();
        hideTimer = setTimeout(() => {
            if (completionPinned || hoveringWidget || hoveringTooltip) {
                return;
            }
            completionTooltip.classList.remove('is-visible');
            completionTooltip.setAttribute('aria-hidden', 'true');
            completionWidget.setAttribute('aria-expanded', 'false');
        }, 70);
    }

    function positionTooltip(clientX, clientY) {
        const width = completionTooltip.offsetWidth || 280;
        const height = completionTooltip.offsetHeight || 150;
        const margin = 14;
        let left = clientX + 18;
        let top = clientY + 18;

        if (left + width > window.innerWidth - margin) {
            left = clientX - width - 18;
        }
        if (top + height > window.innerHeight - margin) {
            top = clientY - height - 18;
        }

        completionTooltip.style.left = `${Math.max(margin, left)}px`;
        completionTooltip.style.top = `${Math.max(margin, top)}px`;
    }

    function showTooltip(clientX, clientY) {
        clearHideTimer();
        completionTooltip.classList.add('is-visible');
        completionTooltip.setAttribute('aria-hidden', 'false');
        completionWidget.setAttribute('aria-expanded', 'true');
        positionTooltip(clientX, clientY);
    }

    function bindActiveAction(item) {
        activeCompletionItem = item;
        if (!item) {
            completionTitle.textContent = 'Profile complete';
            completionMessage.textContent = 'Everything important is filled in and verified.';
            completionAction.textContent = 'All done';
            completionAction.classList.add('is-disabled');
            completionAction.setAttribute('aria-disabled', 'true');
            completionAction.removeAttribute('href');
            return;
        }

        completionTitle.textContent = item.actionLabel;
        completionMessage.textContent = item.message;
        completionAction.textContent = item.actionLabel;
        completionAction.classList.remove('is-disabled');
        completionAction.removeAttribute('aria-disabled');
        completionAction.href = item.target || '#';
    }

    function updateCompletionPrompt() {
        const checklist = computeChecklist();
        const missingCount = checklist.filter((item) => item.missing).length;
        const total = checklist.length;
        const completed = total - missingCount;
        const percent = Math.round((completed * 100) / total);

        if (completionPercent) {
            completionPercent.textContent = `${percent}%`;
        }
        if (completionBar) {
            completionBar.style.width = `${percent}%`;
        }

        completionWidget.style.setProperty('--profile-completion-progress', `${percent}%`);
        bindActiveAction(checklist.find((item) => item.missing) || null);
    }

    const watchSelectors = [
        '#profileImageInput',
        '#profile-username-input',
        '#profile-email-input',
        '#profile-phone-input',
        '#profile-date-of-birth-input',
        '#profile-first-name-input',
        '#profile-last-name-input',
        '#profile-bio-input'
    ];

    watchSelectors.forEach((selector) => {
        const element = document.querySelector(selector);
        if (!element) return;
        element.addEventListener('change', updateCompletionPrompt);
        element.addEventListener('input', updateCompletionPrompt);
    });

    completionWidget.addEventListener('mouseenter', (event) => {
        hoveringWidget = true;
        updateCompletionPrompt();
        // Position tooltip once at mouse position, don't follow mouse
        if (!completionPinned) {
            showTooltip(event.clientX, event.clientY);
        }
    });

    completionWidget.addEventListener('mouseleave', () => {
        hoveringWidget = false;
        if (!completionPinned) {
            scheduleHide();
        }
    });

    completionWidget.addEventListener('focusin', () => {
        updateCompletionPrompt();
        const rect = completionWidget.getBoundingClientRect();
        showTooltip(rect.right, rect.top + 12);
    });

    completionWidget.addEventListener('click', (event) => {
        if (event.target === completionAction) {
            return;
        }
        completionPinned = !completionPinned;
        if (completionPinned) {
            const rect = completionWidget.getBoundingClientRect();
            showTooltip(rect.right, rect.top + 12);
        } else {
            scheduleHide();
        }
    });

    completionTooltip.addEventListener('mouseenter', () => {
        hoveringTooltip = true;
        clearHideTimer();
    });

    completionTooltip.addEventListener('mouseleave', () => {
        hoveringTooltip = false;
        scheduleHide();
    });

    completionAction.addEventListener('click', (event) => {
        if (!activeCompletionItem) {
            event.preventDefault();
            return;
        }
        event.preventDefault();
        completionPinned = false;
        completionTooltip.classList.remove('is-visible');
        completionTooltip.setAttribute('aria-hidden', 'true');
        completionWidget.setAttribute('aria-expanded', 'false');
        scrollToTarget(activeCompletionItem.target, activeCompletionItem.mode);
    });

    document.addEventListener('click', (event) => {
        if (!completionWidget.contains(event.target) && !completionTooltip.contains(event.target)) {
            completionPinned = false;
            hoveringWidget = false;
            hoveringTooltip = false;
            scheduleHide();
        }
    });

    updateCompletionPrompt();
})();

/* Sidebar avatar initials fallback */
(function () {
    if (!profileSidebarAvatarFallback) return;

    function toInitial(value) {
        const text = String(value || '').trim();
        return text ? text.charAt(0).toUpperCase() : '';
    }

    function updateAvatarInitials() {
        const first = toInitial(profileFirstNameInput ? profileFirstNameInput.value : '');
        const last = toInitial(profileLastNameInput ? profileLastNameInput.value : '');
        profileSidebarAvatarFallback.textContent = `${first}${last}` || 'U';
    }

    if (profileFirstNameInput) {
        profileFirstNameInput.addEventListener('input', updateAvatarInitials);
        profileFirstNameInput.addEventListener('change', updateAvatarInitials);
    }

    if (profileLastNameInput) {
        profileLastNameInput.addEventListener('input', updateAvatarInitials);
        profileLastNameInput.addEventListener('change', updateAvatarInitials);
    }

    updateAvatarInitials();
})();

/* Premium profile customiser live preview */
(function () {
    const customiserForm = document.getElementById('profile-customiser-form');
    if (!customiserForm) return;

    const bannerSelect = document.getElementById('customiser-banner');
    const ringSelect = document.getElementById('customiser-ring');
    const backSelect = document.getElementById('customiser-back');
    const textColorInput = document.getElementById('customiser-text-color');
    const generalTextColorInput = document.getElementById('customiser-general-text-color');
    const sidebarCard = document.getElementById('profile-sidebar-card-back');
    const sidebarBanner = document.getElementById('profile-sidebar-banner-pill');
    const sidebarBannerLabel = document.getElementById('profile-sidebar-banner-label');
    const sidebarRing = document.getElementById('profile-sidebar-avatar-ring');
    const sidebarName = document.getElementById('profile-identity-banner__name');
    const sidebarHandle = document.getElementById('profile-identity-banner__handle');
    const sidebarBio = document.getElementById('profile-sidebar-bio-text');
    const usernameInputLive = document.getElementById('profile-username-input');
    const bioInputLive = document.getElementById('profile-bio-input');
    const milestoneChecks = Array.from(customiserForm.querySelectorAll('.customiser-milestone'));
    const milestoneMsg = document.getElementById('customiser-milestone-msg');

    function humanizeToken(value) {
        return (value || '')
            .toLowerCase()
            .split('_')
            .filter(Boolean)
            .map((part) => part.charAt(0).toUpperCase() + part.slice(1))
            .join(' ');
    }

    function mountSearchableSelect(selectEl) {
        if (!selectEl || selectEl.dataset.searchEnhanced === 'true') {
            return;
        }

        const wrapper = document.createElement('div');
        wrapper.className = 'profile-search-select';

        const trigger = document.createElement('button');
        trigger.type = 'button';
        trigger.className = 'profile-search-select__trigger';
        trigger.setAttribute('aria-haspopup', 'listbox');
        trigger.setAttribute('aria-expanded', 'false');

        const triggerText = document.createElement('span');
        triggerText.className = 'profile-search-select__trigger-text';

        const triggerIcon = document.createElement('span');
        triggerIcon.className = 'profile-search-select__trigger-icon';
        triggerIcon.textContent = '▾';

        trigger.appendChild(triggerText);
        trigger.appendChild(triggerIcon);

        const dropdown = document.createElement('div');
        dropdown.className = 'profile-search-select__dropdown hidden';

        const searchInput = document.createElement('input');
        searchInput.type = 'text';
        searchInput.className = 'profile-search-select__search';
        searchInput.placeholder = 'Search options...';

        const listbox = document.createElement('div');
        listbox.className = 'profile-search-select__list';
        listbox.setAttribute('role', 'listbox');

        dropdown.appendChild(searchInput);
        dropdown.appendChild(listbox);

        const originalParent = selectEl.parentNode;
        originalParent.insertBefore(wrapper, selectEl);
        wrapper.appendChild(trigger);
        wrapper.appendChild(dropdown);
        wrapper.appendChild(selectEl);

        selectEl.classList.add('profile-search-select__native');
        selectEl.setAttribute('tabindex', '-1');
        selectEl.setAttribute('aria-hidden', 'true');
        selectEl.dataset.searchEnhanced = 'true';

        function syncTriggerLabel() {
            const option = selectEl.options[selectEl.selectedIndex];
            triggerText.textContent = option ? humanizeToken(option.textContent || option.value) : 'Select option';
        }

        function closeDropdown() {
            dropdown.classList.add('hidden');
            trigger.setAttribute('aria-expanded', 'false');
        }

        function openDropdown() {
            dropdown.classList.remove('hidden');
            trigger.setAttribute('aria-expanded', 'true');
            searchInput.focus();
            searchInput.select();
        }

        function renderOptions(filterText) {
            const term = (filterText || '').trim().toLowerCase();
            const options = Array.from(selectEl.options).filter((opt) => {
                const text = humanizeToken(opt.textContent || opt.value).toLowerCase();
                return text.includes(term);
            });

            listbox.innerHTML = '';
            if (!options.length) {
                const empty = document.createElement('div');
                empty.className = 'profile-search-select__empty';
                empty.textContent = 'No matching styles.';
                listbox.appendChild(empty);
                return;
            }

            options.forEach((opt) => {
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'profile-search-select__option';
                item.setAttribute('role', 'option');
                item.setAttribute('data-value', opt.value);
                item.setAttribute('aria-selected', opt.selected ? 'true' : 'false');
                item.textContent = humanizeToken(opt.textContent || opt.value);
                if (opt.selected) {
                    item.classList.add('is-selected');
                }
                item.addEventListener('click', () => {
                    selectEl.value = opt.value;
                    selectEl.dispatchEvent(new Event('change', { bubbles: true }));
                    syncTriggerLabel();
                    closeDropdown();
                });
                listbox.appendChild(item);
            });
        }

        trigger.addEventListener('click', () => {
            if (dropdown.classList.contains('hidden')) {
                renderOptions(searchInput.value);
                openDropdown();
                return;
            }
            closeDropdown();
        });

        searchInput.addEventListener('input', () => {
            renderOptions(searchInput.value);
        });

        searchInput.addEventListener('keydown', (event) => {
            if (event.key === 'Escape') {
                event.preventDefault();
                closeDropdown();
                trigger.focus();
            }
        });

        document.addEventListener('click', (event) => {
            if (!wrapper.contains(event.target)) {
                closeDropdown();
            }
        });

        selectEl.addEventListener('change', () => {
            syncTriggerLabel();
            renderOptions(searchInput.value);
        });

        syncTriggerLabel();
        renderOptions('');
    }

    [bannerSelect, ringSelect, backSelect].forEach((selectEl) => mountSearchableSelect(selectEl));

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

    function currentTextColor() {
        const value = textColorInput && textColorInput.value ? textColorInput.value.trim() : '#F8FAFC';
        return /^#[0-9A-Fa-f]{6}$/.test(value) ? value.toUpperCase() : '#F8FAFC';
    }

    function currentGeneralTextColor() {
        const value = generalTextColorInput && generalTextColorInput.value ? generalTextColorInput.value.trim() : '#CBD5E1';
        return /^#[0-9A-Fa-f]{6}$/.test(value) ? value.toUpperCase() : '#CBD5E1';
    }

    function applyTextColorPreview(nameTextColor, generalTextColor) {
        [sidebarName].forEach((element) => {
            if (!element) return;
            element.style.color = nameTextColor;
        });

        [sidebarHandle, sidebarBio].forEach((element) => {
            if (!element) return;
            element.style.color = generalTextColor;
        });
    }

    function applyTextPreview() {
        if (sidebarHandle && usernameInputLive) {
            const rawUsername = String(usernameInputLive.value || '').trim();
            sidebarHandle.textContent = rawUsername ? `@${rawUsername}` : '@user';
        }
        if (bioInputLive && sidebarBio) {
            const rawBio = String(bioInputLive.value || '').trim();
            sidebarBio.textContent = rawBio || 'No bio yet — add one in Edit Profile.';
        }
    }

    function applyPreview() {
        const bannerKey = currentBannerKey();
        const ringKey = currentRingKey();
        const backKey = currentBackKey();
        const textColor = currentTextColor();

        // Update sidebar banner (live preview)
        replaceTokenClass(sidebarBanner, 'profile-banner-pill--', bannerKey);
        if (sidebarBanner) {
            sidebarBanner.style.setProperty('--profile-identity-name-color', textColor);
            sidebarBanner.style.setProperty('--profile-identity-handle-color', currentGeneralTextColor());
        }
        if (sidebarBannerLabel && bannerSelect) {
            sidebarBannerLabel.textContent = bannerSelect.value + ' banner';
        }

        // Update sidebar ring (live preview)
        replaceTokenClass(sidebarRing, 'profile-avatar-ring--', ringKey);
        if (sidebarRing) {
            sidebarRing.classList.add('profile-avatar-ring');
        }

        // Update sidebar card back (live preview)
        replaceTokenClass(sidebarCard, 'profile-card-back-theme--', backKey);

        // Update text colours in sidebar live preview only
        applyTextColorPreview(textColor, currentGeneralTextColor());
        applyTextPreview();
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

    [bannerSelect, ringSelect, backSelect, textColorInput, generalTextColorInput].forEach((el) => {
        if (!el) return;
        el.addEventListener('change', applyPreview);
    });

    if (textColorInput) {
        textColorInput.addEventListener('input', applyPreview);
    }

    if (generalTextColorInput) {
        generalTextColorInput.addEventListener('input', applyPreview);
    }

    if (usernameInputLive) {
        usernameInputLive.addEventListener('input', applyPreview);
        usernameInputLive.addEventListener('change', applyPreview);
    }

    if (bioInputLive) {
        bioInputLive.addEventListener('input', applyPreview);
        bioInputLive.addEventListener('change', applyPreview);
    }

    milestoneChecks.forEach((input) => {
        input.addEventListener('change', () => enforceMilestoneCap(input));
    });

    applyPreview();
})();

/* Live preview for name changes */
(function () {
    const firstNameInput = document.getElementById('profile-first-name-input');
    const lastNameInput = document.getElementById('profile-last-name-input');
    const sidebarNameElement = document.getElementById('profile-identity-banner__name');
    
    if (!firstNameInput || !lastNameInput) return;

    function updateSidebarName() {
        const firstName = (firstNameInput.value || '').trim();
        const lastName = (lastNameInput.value || '').trim();

        // Display first name only (as per navbar styling)
        const displayName = firstName || lastName || 'Profile';
        if (sidebarNameElement) {
            sidebarNameElement.textContent = displayName;
        }
    }

    // Add event listeners for live preview
    firstNameInput.addEventListener('input', updateSidebarName);
    lastNameInput.addEventListener('input', updateSidebarName);
    firstNameInput.addEventListener('change', updateSidebarName);
    lastNameInput.addEventListener('change', updateSidebarName);

    // Initial update
    updateSidebarName();
})();

/* Settings Drawer */
const optionsDrawerRoot = document.getElementById('options-drawer-root');
const optionsOverlay    = document.getElementById('options-overlay');
const openOptionsBtn    = document.getElementById('open-options-drawer');
const closeOptionsBtn   = document.getElementById('close-options-drawer');

function openOptionsDrawer() {
    if (!optionsDrawerRoot) return;
    optionsDrawerRoot.classList.add('is-open');
    optionsDrawerRoot.setAttribute('aria-hidden', 'false');
    document.body.style.overflow = 'hidden';
    if (openOptionsBtn) {
        openOptionsBtn.classList.add('is-open');
        openOptionsBtn.setAttribute('aria-expanded', 'true');
    }
}
window.openOptionsDrawer = openOptionsDrawer;

function closeOptionsDrawer() {
    if (!optionsDrawerRoot) return;
    optionsDrawerRoot.classList.remove('is-open');
    optionsDrawerRoot.setAttribute('aria-hidden', 'true');
    document.body.style.overflow = '';
    if (openOptionsBtn) {
        openOptionsBtn.classList.remove('is-open');
        openOptionsBtn.setAttribute('aria-expanded', 'false');
        openOptionsBtn.focus();
    }
}

if (openOptionsBtn)  openOptionsBtn.addEventListener('click', () => {
    if (optionsDrawerRoot && optionsDrawerRoot.classList.contains('is-open')) {
        closeOptionsDrawer();
        return;
    }
    openOptionsDrawer();
});
if (closeOptionsBtn) closeOptionsBtn.addEventListener('click', closeOptionsDrawer);
if (optionsOverlay)  optionsOverlay.addEventListener('click', closeOptionsDrawer);

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
        } else if (optionsDrawerRoot && optionsDrawerRoot.classList.contains('is-open')) {
            closeOptionsDrawer();
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

/* Calendar display auto-save */
(function () {
    const form = document.getElementById('calendar-display-form');
    const feedback = document.getElementById('calendar-display-feedback');
    if (!form) return;

    const radios = Array.from(form.querySelectorAll('input[name="layout"]'));
    if (!radios.length) return;

    function showFeedback(message, isError) {
        if (!feedback) return;
        feedback.textContent = message;
        feedback.classList.remove('hidden', 'text-emerald-600', 'dark:text-emerald-400', 'text-rose-600', 'dark:text-rose-400');
        if (isError) {
            feedback.classList.add('text-rose-600', 'dark:text-rose-400');
        } else {
            feedback.classList.add('text-emerald-600', 'dark:text-emerald-400');
        }
        clearTimeout(feedback._timeout);
        feedback._timeout = setTimeout(() => feedback.classList.add('hidden'), isError ? 3200 : 2200);
    }

    function setSaving(disabled) {
        radios.forEach((radio) => {
            radio.disabled = disabled;
        });
    }

    async function saveCalendarLayout(layout) {
        const csrfInput = form.querySelector('input[name="_csrf"]') || document.querySelector('input[name="_csrf"]');
        const csrfName = csrfInput ? csrfInput.name : '_csrf';
        const csrfToken = csrfInput ? csrfInput.value : '';
        const body = new URLSearchParams({ layout });
        if (csrfToken) {
            body.append(csrfName, csrfToken);
        }

        setSaving(true);
        try {
            const response = await fetch('/profile/settings/calendar-display', {
                method: 'POST',
                headers: {
                    'Content-Type': 'application/x-www-form-urlencoded',
                    'Accept': 'text/html,application/json'
                },
                body: body.toString(),
                credentials: 'same-origin'
            });

            if (!response.ok) {
                throw new Error('Could not save calendar display.');
            }

            showFeedback('Calendar display saved.', false);
        } catch (_error) {
            showFeedback('Could not save display. Please try again.', true);
        } finally {
            setSaving(false);
        }
    }

    radios.forEach((radio) => {
        radio.addEventListener('change', () => {
            if (!radio.checked) return;
            saveCalendarLayout(radio.value);
        });
    });

    form.addEventListener('submit', (event) => {
        event.preventDefault();
    });
})();

/* Custom DOB Awareness Modal */
function showDOBAwarenessModal() {
    const modal = document.getElementById('dob-awareness-modal');
    const confirmBtn = document.getElementById('dob-modal-confirm');
    const cancelBtn = document.getElementById('dob-modal-cancel');
    
    if (!modal) return;

    // Show modal
    modal.classList.add('is-visible');
    document.body.style.overflow = 'hidden';

    function closeModal() {
        modal.classList.remove('is-visible');
        document.body.style.overflow = '';
    }

    function handleConfirm() {
        closeModal();
        removeDOBModalListeners();
        // Submit the form
        if (saveProfileButton) {
            saveProfileButton.click();
        } else if (editForm) {
            editForm.submit();
        }
    }

    function handleCancel() {
        closeModal();
        removeDOBModalListeners();
        if (profileDateOfBirthInput) {
            profileDateOfBirthInput.focus();
        }
    }

    function handleBackdropClick(e) {
        if (e.target === modal.querySelector('.dob-awareness-modal__backdrop')) {
            handleCancel();
        }
    }

    function handleEscapeKey(e) {
        if (e.key === 'Escape') {
            handleCancel();
        }
    }

    function removeDOBModalListeners() {
        if (confirmBtn) confirmBtn.removeEventListener('click', handleConfirm);
        if (cancelBtn) cancelBtn.removeEventListener('click', handleCancel);
        modal.removeEventListener('click', handleBackdropClick);
        document.removeEventListener('keydown', handleEscapeKey);
    }

    // Attach event listeners
    if (confirmBtn) confirmBtn.addEventListener('click', handleConfirm);
    if (cancelBtn) cancelBtn.addEventListener('click', handleCancel);
    modal.addEventListener('click', handleBackdropClick);
    document.addEventListener('keydown', handleEscapeKey);

    // Focus confirm button for accessibility
    if (confirmBtn) {
        setTimeout(() => confirmBtn.focus(), 100);
    }
}
