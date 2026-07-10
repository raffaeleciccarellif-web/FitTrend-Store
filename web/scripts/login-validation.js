document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("loginForm");
    const emailInput = document.getElementById("email");
    const passwordInput = document.getElementById("password");
    
    const emailError = document.getElementById("email-error");
    const passwordError = document.getElementById("password-error");
    
    const emailRegex = /^[^\s@]+@[^\s@]+\.[^\s@]+$/;
    
    function validateEmail() {
        const email = emailInput.value.trim();
        if (email === "") {
            emailError.textContent = "L'email è obbligatoria.";
            emailInput.classList.add("error-input");
            return false;
        } else if (!emailRegex.test(email)) {
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
        const password = passwordInput.value; // non fare trim della password, gli spazi potrebbero essere voluti, ma per la verifica del vuoto ci può stare
        if (password.trim() === "") {
            passwordError.textContent = "La password è obbligatoria.";
            passwordInput.classList.add("error-input");
            return false;
        } else {
            passwordError.textContent = "";
            passwordInput.classList.remove("error-input");
            return true;
        }
    }
    
    emailInput.addEventListener("input", validateEmail);
    emailInput.addEventListener("change", validateEmail);
    
    passwordInput.addEventListener("input", validatePassword);
    passwordInput.addEventListener("change", validatePassword);
    
    form.addEventListener("submit", function(event) {
        const isEmailValid = validateEmail();
        const isPasswordValid = validatePassword();
        
        if (!isEmailValid || !isPasswordValid) {
            event.preventDefault(); // Blocca l'invio del form
        }
    });
});
