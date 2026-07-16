document.addEventListener("DOMContentLoaded", function() {
    const checkoutForm = document.getElementById("checkoutForm");
    const metodoPagamento = document.getElementById("metodoPagamento");
    const cartaContainer = document.getElementById("cartaContainer");
    const numeroCarta = document.getElementById("numeroCarta");
    
    const indirizzoSpedizione = document.getElementById("indirizzoSpedizione");
    const cittaSpedizione = document.getElementById("cittaSpedizione");
    const capSpedizione = document.getElementById("capSpedizione");

    if (metodoPagamento) {
        metodoPagamento.addEventListener("change", function() {
            if (metodoPagamento.value === "carta") {
                cartaContainer.style.display = "block";
            } else {
                cartaContainer.style.display = "none";
                numeroCarta.value = "";
                document.getElementById("numeroCartaError").textContent = "";
            }
            validateMetodoPagamento();
        });
    }

    function validateIndirizzo() {
        if (indirizzoSpedizione.value.trim() === "") {
            document.getElementById("indirizzoSpedizioneError").textContent = "L'indirizzo di spedizione è obbligatorio.";
            return false;
        } else {
            document.getElementById("indirizzoSpedizioneError").textContent = "";
            return true;
        }
    }

    function validateCitta() {
        if (cittaSpedizione.value.trim() === "") {
            document.getElementById("cittaSpedizioneError").textContent = "La città è obbligatoria.";
            return false;
        } else {
            document.getElementById("cittaSpedizioneError").textContent = "";
            return true;
        }
    }

    function validateCap() {
        const capRegex = /^[0-9]{5}$/;
        if (!capRegex.test(capSpedizione.value.trim())) {
            document.getElementById("capSpedizioneError").textContent = "Il CAP deve contenere esattamente 5 cifre.";
            return false;
        } else {
            document.getElementById("capSpedizioneError").textContent = "";
            return true;
        }
    }

    function validateMetodoPagamento() {
        if (metodoPagamento.value === "") {
            document.getElementById("metodoPagamentoError").textContent = "Seleziona un metodo di pagamento.";
            return false;
        } else {
            document.getElementById("metodoPagamentoError").textContent = "";
            return true;
        }
    }

    function validateNumeroCarta() {
        if (metodoPagamento.value === "carta") {
            const cartaRegex = /^[0-9]{13,19}$/;
            if (!cartaRegex.test(numeroCarta.value.trim())) {
                document.getElementById("numeroCartaError").textContent = "Il numero di carta deve contenere tra 13 e 19 cifre.";
                return false;
            } else {
                document.getElementById("numeroCartaError").textContent = "";
                return true;
            }
        }
        return true;
    }

    if (indirizzoSpedizione) indirizzoSpedizione.addEventListener("input", validateIndirizzo);
    if (cittaSpedizione) cittaSpedizione.addEventListener("input", validateCitta);
    if (capSpedizione) capSpedizione.addEventListener("input", validateCap);
    if (numeroCarta) numeroCarta.addEventListener("input", validateNumeroCarta);

    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function(event) {
            const isIndirizzoValid = validateIndirizzo();
            const isCittaValid = validateCitta();
            const isCapValid = validateCap();
            const isMetodoValid = validateMetodoPagamento();
            const isCartaValid = validateNumeroCarta();

            if (!isIndirizzoValid || !isCittaValid || !isCapValid || !isMetodoValid || !isCartaValid) {
                event.preventDefault();
            }
        });
    }
});
