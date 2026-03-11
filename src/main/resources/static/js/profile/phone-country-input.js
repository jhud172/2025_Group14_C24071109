/* Phone country + immutable prefix handler */
(function () {
    const countryCodesMap = {
        GB: '+44',
        US: '+1',
        CA: '+1',
        AU: '+61',
        NZ: '+64',
        IE: '+353',
        DE: '+49',
        FR: '+33',
        NL: '+31',
        BE: '+32',
        ES: '+34',
        IT: '+39',
        JP: '+81',
        IN: '+91',
        BR: '+55',
        MX: '+52',
        SG: '+65',
        HK: '+852',
        ZA: '+27'
    };

    const countrySelect = document.getElementById('profile-phone-country');
    const phoneInput = document.getElementById('profile-phone-input');

    if (!countrySelect || !phoneInput) {
        return;
    }

    function countryNameFromOption(option) {
        const text = String(option && option.textContent ? option.textContent : '').trim();
        if (!text) return option && option.value ? option.value : '';
        const dashIndex = text.indexOf('-');
        if (dashIndex > -1) {
            return text.substring(dashIndex + 1).trim();
        }
        return text.replace(/^[^A-Za-z]+\s*/, '').trim();
    }

    function mountCustomCountrySelect() {
        if (countrySelect.dataset.customMounted === 'true') {
            return;
        }

        const wrapper = document.createElement('div');
        wrapper.className = 'profile-phone-country-wrapper';

        const trigger = document.createElement('button');
        trigger.type = 'button';
        trigger.className = 'profile-phone-country-trigger';
        trigger.setAttribute('aria-haspopup', 'listbox');
        trigger.setAttribute('aria-expanded', 'false');

        const triggerText = document.createElement('span');
        triggerText.className = 'profile-phone-country-text';
        triggerText.textContent = String(countrySelect.value || 'GB').toUpperCase();

        const triggerIcon = document.createElement('span');
        triggerIcon.className = 'profile-phone-country-icon';
        triggerIcon.textContent = '▾';

        trigger.appendChild(triggerText);
        trigger.appendChild(triggerIcon);

        const dropdown = document.createElement('div');
        dropdown.className = 'profile-phone-country-dropdown hidden';
        dropdown.setAttribute('role', 'listbox');

        const list = document.createElement('div');
        list.className = 'profile-phone-country-list';
        dropdown.appendChild(list);

        function renderOptions() {
            list.innerHTML = '';
            Array.from(countrySelect.options).forEach((option) => {
                const value = String(option.value || '').toUpperCase();
                const item = document.createElement('button');
                item.type = 'button';
                item.className = 'profile-phone-country-option';
                item.setAttribute('role', 'option');
                item.setAttribute('data-value', value);
                item.textContent = `${value} - ${countryNameFromOption(option)}`;

                const isSelected = value === String(countrySelect.value || '').toUpperCase();
                item.classList.toggle('is-selected', isSelected);
                item.setAttribute('aria-selected', isSelected ? 'true' : 'false');

                item.addEventListener('click', () => {
                    countrySelect.value = value;
                    triggerText.textContent = value;
                    countrySelect.dispatchEvent(new Event('change', { bubbles: true }));
                    closeDropdown();
                    renderOptions();
                });

                list.appendChild(item);
            });
        }

        function openDropdown() {
            dropdown.classList.remove('hidden');
            wrapper.classList.add('is-open');
            trigger.setAttribute('aria-expanded', 'true');
        }

        function closeDropdown() {
            dropdown.classList.add('hidden');
            wrapper.classList.remove('is-open');
            trigger.setAttribute('aria-expanded', 'false');
        }

        trigger.addEventListener('click', () => {
            if (dropdown.classList.contains('hidden')) {
                openDropdown();
            } else {
                closeDropdown();
            }
        });

        document.addEventListener('click', (event) => {
            if (!wrapper.contains(event.target)) {
                closeDropdown();
            }
        });

        countrySelect.addEventListener('change', () => {
            triggerText.textContent = String(countrySelect.value || 'GB').toUpperCase();
            renderOptions();
        });

        const parent = countrySelect.parentNode;
        parent.insertBefore(wrapper, countrySelect);
        wrapper.appendChild(trigger);
        wrapper.appendChild(dropdown);
        wrapper.appendChild(countrySelect);

        countrySelect.classList.add('hidden');
        countrySelect.setAttribute('aria-hidden', 'true');
        countrySelect.dataset.customMounted = 'true';

        renderOptions();
    }

    const normalizedCountry = String(countrySelect.value || 'GB').toUpperCase();
    const initialPrefix = countryCodesMap[normalizedCountry] || '+44';

    function currentPrefix() {
        const key = String(countrySelect.value || 'GB').toUpperCase();
        return (countryCodesMap[key] || '+44') + ' ';
    }

    function stripExistingPrefix(value) {
        return String(value || '').replace(/^\+\d+\s*/, '').trimStart();
    }

    function applyPrefix(preserveLocalDigits) {
        const prefix = currentPrefix();
        const local = preserveLocalDigits ? stripExistingPrefix(phoneInput.value) : '';
        phoneInput.value = prefix + local;
        phoneInput.dataset.phonePrefix = prefix;
        phoneInput.setSelectionRange(prefix.length, prefix.length);
    }

    function enforcePrefixBoundary() {
        const prefix = phoneInput.dataset.phonePrefix || currentPrefix();
        const minPos = prefix.length;
        if (phoneInput.selectionStart < minPos || phoneInput.selectionEnd < minPos) {
            phoneInput.setSelectionRange(minPos, minPos);
        }
    }

    function sanitizeToDigitsAndFormatting(value) {
        const prefix = phoneInput.dataset.phonePrefix || currentPrefix();
        const stripped = String(value || '').startsWith(prefix)
            ? String(value || '').slice(prefix.length)
            : stripExistingPrefix(value);
        const digitsOnly = stripped.replace(/\D/g, '');
        const noLeadingZero = digitsOnly.replace(/^0+/, '');
        const maxLocalDigits = 10;
        const limited = noLeadingZero.slice(0, maxLocalDigits);
        return prefix + limited;
    }

    countrySelect.addEventListener('change', () => {
        applyPrefix(true);
        phoneInput.focus();
    });

    phoneInput.addEventListener('focus', enforcePrefixBoundary);
    phoneInput.addEventListener('click', enforcePrefixBoundary);

    phoneInput.addEventListener('keydown', (event) => {
        const prefix = phoneInput.dataset.phonePrefix || currentPrefix();
        const boundary = prefix.length;
        const start = phoneInput.selectionStart;
        const end = phoneInput.selectionEnd;
        const touchesPrefix = start < boundary || end < boundary;

        if (!touchesPrefix) {
            return;
        }

        const navKeys = new Set(['ArrowLeft', 'ArrowRight', 'ArrowUp', 'ArrowDown', 'Tab', 'Shift', 'Control', 'Alt']);
        if (navKeys.has(event.key)) {
            return;
        }

        if (event.key === 'Home') {
            event.preventDefault();
            phoneInput.setSelectionRange(boundary, boundary);
            return;
        }

        if (event.key === 'Backspace' || event.key === 'Delete' || event.key.length === 1) {
            event.preventDefault();
            phoneInput.setSelectionRange(boundary, boundary);
        }
    });

    phoneInput.addEventListener('input', () => {
        phoneInput.value = sanitizeToDigitsAndFormatting(phoneInput.value);
        enforcePrefixBoundary();
    });

    // Initialize with selected country prefix. Preserve existing number after any old prefix.
    const hasInitialValue = String(phoneInput.value || '').trim().length > 0;
    phoneInput.value = hasInitialValue
        ? (initialPrefix + ' ' + stripExistingPrefix(phoneInput.value))
        : (initialPrefix + ' ');
    phoneInput.dataset.phonePrefix = initialPrefix + ' ';

    mountCustomCountrySelect();
})();
