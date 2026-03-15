(function () {
    "use strict";

    const pageRoot = document.querySelector("[data-login-server-role]");
    const roleOptions = document.querySelectorAll(".role-slider__option");
    const loginTypeInput = document.getElementById("loginType");
    const emailFields = document.getElementById("emailFields");
    const gymFields = document.getElementById("gymFields");
    const trainerCodeField = document.getElementById("trainerCodeField");
    const roleHint = document.getElementById("roleHint");
    const usernameInput = document.getElementById("username");
    const passwordInput = document.getElementById("password");
    const gymCodeInput = document.getElementById("gymCode");
    const gymPasswordInput = document.getElementById("gymPassword");
    const togglePassword = document.getElementById("togglePassword");
    const eyeIcon = document.getElementById("eyeIcon");
    const toggleGymPassword = document.getElementById("toggleGymPassword");
    const gymEyeIcon = document.getElementById("gymEyeIcon");
    const trainerCode1 = document.getElementById("trainerCode1");
    const trainerCode2 = document.getElementById("trainerCode2");
    const trainerCode3 = document.getElementById("trainerCode3");
    const trainerCodeFull = document.getElementById("trainerCodeFull");

    if (!loginTypeInput || !emailFields || !gymFields || !trainerCodeField || !roleHint) {
        return;
    }

    const hints = {
        client: pageRoot && pageRoot.dataset.clientHint ? pageRoot.dataset.clientHint : "Login with your email and password.",
        trainer: pageRoot && pageRoot.dataset.trainerHint ? pageRoot.dataset.trainerHint : "Login with email, password, and trainer code.",
        gym: pageRoot && pageRoot.dataset.gymHint ? pageRoot.dataset.gymHint : "Login with your gym code and password."
    };

    function setPasswordIcon(icon, visible) {
        if (!icon) {
            return;
        }
        icon.innerHTML = visible
            ? '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M13.875 18.825A10.05 10.05 0 0112 19c-4.478 0-8.268-2.943-9.543-7a9.97 9.97 0 011.563-3.029m5.858.908a3 3 0 114.243 4.243M9.878 9.878l4.242 4.242M9.88 9.88l-3.29-3.29m7.532 7.532l3.29 3.29M3 3l3.59 3.59m0 0A9.953 9.953 0 0112 5c4.478 0 8.268 2.943 9.543 7a10.025 10.025 0 01-4.132 5.411m0 0L21 21" />'
            : '<path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M15 12a3 3 0 11-6 0 3 3 0 016 0z" /><path stroke-linecap="round" stroke-linejoin="round" stroke-width="2" d="M2.458 12C3.732 7.943 7.523 5 12 5c4.478 0 8.268 2.943 9.542 7-1.274 4.057-5.064 7-9.542 7-4.477 0-8.268-2.943-9.542-7z" />';
    }

    function activateRole(role, animate) {
        roleOptions.forEach((option) => option.classList.remove("active"));
        const target = document.querySelector('[data-role="' + role + '"]');
        if (target) {
            target.classList.add("active");
        }

        loginTypeInput.value = role;
        roleHint.textContent = hints[role] || hints.client;
        sessionStorage.setItem("authRole", role);

        const delay = animate ? 200 : 0;
        if (role === "gym") {
            emailFields.classList.add("hidden");
            window.setTimeout(() => gymFields.classList.remove("hidden"), delay);
            trainerCodeField.classList.add("hidden");

            if (usernameInput) {
                usernameInput.removeAttribute("required");
                usernameInput.name = "";
            }
            if (passwordInput) {
                passwordInput.removeAttribute("required");
                passwordInput.name = "";
            }
            if (gymCodeInput) {
                gymCodeInput.setAttribute("required", "required");
                gymCodeInput.name = "username";
            }
            if (gymPasswordInput) {
                gymPasswordInput.setAttribute("required", "required");
                gymPasswordInput.name = "password";
            }
            return;
        }

        gymFields.classList.add("hidden");
        window.setTimeout(() => emailFields.classList.remove("hidden"), delay);

        if (usernameInput) {
            usernameInput.setAttribute("required", "required");
            usernameInput.name = "username";
        }
        if (passwordInput) {
            passwordInput.setAttribute("required", "required");
            passwordInput.name = "password";
        }
        if (gymCodeInput) {
            gymCodeInput.removeAttribute("required");
            gymCodeInput.name = "";
        }
        if (gymPasswordInput) {
            gymPasswordInput.removeAttribute("required");
            gymPasswordInput.name = "";
        }

        trainerCodeField.classList.toggle("hidden", role !== "trainer");
    }

    roleOptions.forEach((option) => {
        option.addEventListener("click", () => activateRole(option.dataset.role, true));
    });

    const serverRole = pageRoot ? pageRoot.dataset.loginServerRole : "";
    const storedRole = sessionStorage.getItem("authRole");
    activateRole(serverRole || storedRole || "client", false);

    if (togglePassword && passwordInput) {
        togglePassword.addEventListener("click", () => {
            const visible = passwordInput.getAttribute("type") === "password";
            passwordInput.setAttribute("type", visible ? "text" : "password");
            setPasswordIcon(eyeIcon, visible);
        });
    }

    if (toggleGymPassword && gymPasswordInput) {
        toggleGymPassword.addEventListener("click", () => {
            const visible = gymPasswordInput.getAttribute("type") === "password";
            gymPasswordInput.setAttribute("type", visible ? "text" : "password");
            setPasswordIcon(gymEyeIcon, visible);
        });
    }

    function updateTrainerCode() {
        if (!trainerCodeFull || !trainerCode1 || !trainerCode2 || !trainerCode3) {
            return;
        }
        trainerCodeFull.value = trainerCode1.value + trainerCode2.value + trainerCode3.value;
    }

    [trainerCode1, trainerCode2, trainerCode3].forEach((input, index, inputs) => {
        if (!input) {
            return;
        }

        input.addEventListener("input", (event) => {
            updateTrainerCode();
            if (event.target.value.length === 4 && index < inputs.length - 1) {
                inputs[index + 1].focus();
            }
        });

        input.addEventListener("keydown", (event) => {
            if (event.key === "Backspace" && event.target.value.length === 0 && index > 0) {
                inputs[index - 1].focus();
            }
        });
    });
})();
