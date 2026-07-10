document.addEventListener("DOMContentLoaded", function () {
    const form = document.getElementById("registerForm");
    const nomeInput = document.getElementById("nome");
    const cognomeInput = document.getElementById("cognome");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");

    const nomeError = document.getElementById("nome-error");
    const cognomeError = document.getElementById("cognome-error");
    const emailError = document.getElementById("email-error");
    const passwordError = document.getElementById("password-error");

    const emailRegex = /^[A-Za-z0-9+_.-]+@(.+)$/;
    const passwordRegex = /^(?=.*[A-Z])(?=.*\d).{8,}$/;

    function validateNome() {
        if (!nomeInput.value.trim()) {
            nomeError.textContent = "Il nome è obbligatorio.";
            nomeInput.classList.add("error-input");
            return false;
        } else {
            nomeError.textContent = "";
            nomeInput.classList.remove("error-input");
            return true;
        }
    }

    function validateCognome() {
        if (!cognomeInput.value.trim()) {
            cognomeError.textContent = "Il cognome è obbligatorio.";
            cognomeInput.classList.add("error-input");
            return false;
        } else {
            cognomeError.textContent = "";
            cognomeInput.classList.remove("error-input");
            return true;
        }
    }

    function validateEmail() {
        const val = emailInput.value.trim();
        if (!val) {
            emailError.textContent = "L'email è obbligatoria.";
            emailInput.classList.add("error-input");
            return false;
        } else if (!emailRegex.test(val)) {
            emailError.textContent = "Inserisci un indirizzo email valido.";
            emailInput.classList.add("error-input");
            return false;
        } else {
            emailError.textContent = "";
            emailInput.classList.remove("error-input");
            return true;
        }
    }

    function validatePassword() {
        const val = passwordInput.value;
        if (!val) {
            passwordError.textContent = "La password è obbligatoria.";
            passwordInput.classList.add("error-input");
            return false;
        } else if (!passwordRegex.test(val)) {
            passwordError.textContent = "La password deve contenere almeno 8 caratteri, una maiuscola e un numero.";
            passwordInput.classList.add("error-input");
            return false;
        } else {
            passwordError.textContent = "";
            passwordInput.classList.remove("error-input");
            return true;
        }
    }

    form.addEventListener("submit", function (event) {
        const isNomeValid = validateNome();
        const isCognomeValid = validateCognome();
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();

        if (!isNomeValid || !isCognomeValid || !isEmailValid || !isPasswordValid) {
            event.preventDefault(); // Blocca l'invio del modulo
        }
    });
});
