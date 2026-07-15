document.addEventListener("DOMContentLoaded", function() {
    const checkoutForm = document.getElementById("checkoutForm");
    const metodoPagamento = document.getElementById("metodoPagamento");
    const cartaContainer = document.getElementById("cartaContainer");
    const numeroCarta = document.getElementById("numeroCarta");

    if (metodoPagamento) {
        metodoPagamento.addEventListener("change", function() {
            if (metodoPagamento.value === "carta") {
                cartaContainer.style.display = "block";
                } else {
                cartaContainer.style.display = "none";
                numeroCarta.value = "";
                document.getElementById("numeroCartaError").textContent = "";
            }
        });
    }

    if (checkoutForm) {
        checkoutForm.addEventListener("submit", function(event) {
            let valid = true;


            document.querySelectorAll(".error-msg").forEach(el => el.textContent = "");

            const indirizzoSpedizione = document.getElementById("indirizzoSpedizione").value.trim();
            if (indirizzoSpedizione === "") {
                document.getElementById("indirizzoSpedizioneError").textContent = "L'indirizzo di spedizione è obbligatorio.";
                valid = false;
            }

            const cittaSpedizione = document.getElementById("cittaSpedizione").value.trim();
            if (cittaSpedizione === "") {
                document.getElementById("cittaSpedizioneError").textContent = "La città è obbligatoria.";
                valid = false;
            }

            const capSpedizione = document.getElementById("capSpedizione").value.trim();
            const capRegex = /^[0-9]{5}$/;
            if (!capRegex.test(capSpedizione)) {
                document.getElementById("capSpedizioneError").textContent = "Il CAP deve contenere esattamente 5 cifre.";
                valid = false;
            }

            const metodo = metodoPagamento.value;
            if (metodo === "") {
                document.getElementById("metodoPagamentoError").textContent = "Seleziona un metodo di pagamento.";
                valid = false;
            }

            if (metodo === "carta") {
                const carta = numeroCarta.value.trim();
                const cartaRegex = /^[0-9]{13,19}$/;
                if (!cartaRegex.test(carta)) {
                    document.getElementById("numeroCartaError").textContent = "Il numero di carta deve contenere tra 13 e 19 cifre.";
                    valid = false;
                }
            }

            if (!valid) {
                event.preventDefault();
            }
        });
    }
});
