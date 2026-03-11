// Role slider functionality with sessionStorage persistence
const roleOptions = document.querySelectorAll('.role-slider__option');
const loginTypeInput = document.getElementById('loginType');
const emailFields = document.getElementById('emailFields');
const gymFields = document.getElementById('gymFields');
const trainerCodeField = document.getElementById('trainerCodeField');
const roleHint = document.getElementById('roleHint');
const usernameInput = document.getElementById('username');
const passwordInput = document.getElementById('password');
const gymCodeInput = document.getElementById('gymCode');
const gymPasswordInput = document.getElementById('gymPassword');

const hints = {
    client: 'Login with your email and password.',
    trainer: 'Login with email, password, and trainer code.',
    gym: 'Login with your gym code and password.'
};

function activateRole(role, animate) {
    roleOptions.forEach(opt => opt.classList.remove('active'));
    const target = document.querySelector('[data-role="' + role + '"]');
    if (target) target.classList.add('active');

    loginTypeInput.value = role;
    roleHint.textContent = hints[role] || hints.client;

    // Persist in sessionStorage
    sessionStorage.setItem('authRole', role);

    const delay = animate ? 200 : 0;
    if (role === 'gym') {
        emailFields.classList.add('hidden');
        setTimeout(() => gymFields.classList.remove('hidden'), delay);
        trainerCodeField.classList.add('hidden');

        usernameInput.removeAttribute('required');
        passwordInput.removeAttribute('required');
        passwordInput.name = '';
        usernameInput.name = '';

        gymCodeInput.setAttribute('required', 'required');
        gymPasswordInput.setAttribute('required', 'required');
        gymCodeInput.name = 'username';
        gymPasswordInput.name = 'password';
    } else {
        gymFields.classList.add('hidden');
        setTimeout(() => emailFields.classList.remove('hidden'), delay);

        usernameInput.setAttribute('required', 'required');
        passwordInput.setAttribute('required', 'required');
        usernameInput.name = 'username';
        passwordInput.name = 'password';

        gymCodeInput.removeAttribute('required');
        gymPasswordInput.removeAttribute('required');
        gymCodeInput.name = '';
        gymPasswordInput.name = '';

        if (role === 'trainer') {
            trainerCodeField.classList.remove('hidden');
        } else {
            trainerCodeField.classList.add('hidden');
        }
    }
}

roleOptions.forEach(option => {
    option.addEventListener('click', () => activateRole(option.dataset.role, true));
});

// Determine initial role: server preselection > sessionStorage > default (client)
var serverRole = (typeof loginServerRole !== 'undefined') ? loginServerRole : null;
const storedRole = sessionStorage.getItem('authRole');
const initialRole = serverRole || storedRole || 'client';
activateRole(initialRole, false);

// Password visibility toggle for client/trainer
const togglePassword = document.getElementById('togglePassword');
const eyeIcon = document.getElementById('eyeIcon');

togglePassword.addEventListener('click', () => {
    const type = passwordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    passwordInput.setAttribute('type', type);
    
    if (type === 'text') {
        eyeIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />';
    } else {
        eyeIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />';
    }
});

// Password visibility toggle for gym
const toggleGymPassword = document.getElementById('toggleGymPassword');
const gymEyeIcon = document.getElementById('gymEyeIcon');

toggleGymPassword.addEventListener('click', () => {
    const type = gymPasswordInput.getAttribute('type') === 'password' ? 'text' : 'password';
    gymPasswordInput.setAttribute('type', type);
    
    if (type === 'text') {
        gymEyeIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />';
    } else {
        gymEyeIcon.innerHTML = '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />';
    }
});

// Trainer code auto-advance
const trainerCode1 = document.getElementById('trainerCode1');
const trainerCode2 = document.getElementById('trainerCode2');
const trainerCode3 = document.getElementById('trainerCode3');
const trainerCodeFull = document.getElementById('trainerCodeFull');

function updateTrainerCode() {
    const code = trainerCode1.value + trainerCode2.value + trainerCode3.value;
    trainerCodeFull.value = code;
}

[trainerCode1, trainerCode2, trainerCode3].forEach((input, index, inputs) => {
    input.addEventListener('input', (e) => {
        updateTrainerCode();
        if (e.target.value.length === 4 && index < inputs.length - 1) {
            inputs[index + 1].focus();
        }
    });
    
    input.addEventListener('keydown', (e) => {
        if (e.key === 'Backspace' && e.target.value.length === 0 && index > 0) {
            inputs[index - 1].focus();
        }
    });
});
