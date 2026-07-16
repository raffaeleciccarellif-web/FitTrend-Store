document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("adminProductForm");
    
    if (form) {
        const nome = document.getElementById("nome");
        const categoriaId = document.getElementById("categoriaId");
        const prezzo = document.getElementById("prezzo");
        const quantita = document.getElementById("quantita");
        const immagine = document.getElementById("immagine");
        const descrizione = document.getElementById("descrizione");

        function showError(elementId, message) {
            const el = document.getElementById(elementId);
            let errorDiv = document.getElementById(elementId + "-error");
            if (!errorDiv) {
                errorDiv = document.createElement("div");
                errorDiv.id = elementId + "-error";
                errorDiv.className = "js-inline-error";
                errorDiv.style.color = "#e53e3e";
                errorDiv.style.fontSize = "0.85rem";
                errorDiv.style.marginTop = "0.25rem";
                el.parentNode.insertBefore(errorDiv, el.nextSibling);
            }
            errorDiv.textContent = message;
            el.style.borderColor = "#e53e3e";
        }

        function clearError(elementId) {
            const el = document.getElementById(elementId);
            const errorDiv = document.getElementById(elementId + "-error");
            if (errorDiv) {
                errorDiv.textContent = "";
            }
            if (el) {
                el.style.borderColor = "#ccc";
            }
        }

        function validateNome() {
            if (nome.value.trim() === "") {
                showError("nome", "Il nome del prodotto è obbligatorio.");
                return false;
            }
            clearError("nome");
            return true;
        }

        function validateCategoria() {
            if (categoriaId.value === "") {
                showError("categoriaId", "Devi selezionare una categoria.");
                return false;
            }
            clearError("categoriaId");
            return true;
        }

        function validatePrezzo() {
            const val = parseFloat(prezzo.value);
            if (isNaN(val) || val <= 0) {
                showError("prezzo", "Il prezzo deve essere maggiore di 0.");
                return false;
            }
            clearError("prezzo");
            return true;
        }

        function validateQuantita() {
            const val = parseInt(quantita.value, 10);
            if (isNaN(val) || val < 0) {
                showError("quantita", "La quantità in magazzino non può essere negativa.");
                return false;
            }
            clearError("quantita");
            return true;
        }

        function validateImmagine() {
            if (immagine.value.trim() === "") {
                showError("immagine", "Il percorso dell'immagine è obbligatorio.");
                return false;
            }
            clearError("immagine");
            return true;
        }

        function validateDescrizione() {
            if (descrizione.value.trim() === "") {
                showError("descrizione", "La descrizione è obbligatoria.");
                return false;
            }
            clearError("descrizione");
            return true;
        }

        if (nome) nome.addEventListener("input", validateNome);
        if (categoriaId) categoriaId.addEventListener("change", validateCategoria);
        if (prezzo) prezzo.addEventListener("input", validatePrezzo);
        if (quantita) quantita.addEventListener("input", validateQuantita);
        if (immagine) immagine.addEventListener("input", validateImmagine);
        if (descrizione) descrizione.addEventListener("input", validateDescrizione);

        form.addEventListener("submit", function(event) {
            const v1 = validateNome();
            const v2 = validateCategoria();
            const v3 = validatePrezzo();
            const v4 = validateQuantita();
            const v5 = validateImmagine();
            const v6 = validateDescrizione();

            if (!v1 || !v2 || !v3 || !v4 || !v5 || !v6) {
                event.preventDefault();
            }
        });
    }
});
