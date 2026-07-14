document.addEventListener("DOMContentLoaded", function() {
    const form = document.getElementById("adminProductForm");

    if (form) {
        form.addEventListener("submit", function(event) {
            
            // Pulisce messaggi di errore precedenti
            let oldError = form.querySelector(".js-error-msg");
            if (oldError) oldError.remove();

            let errors = [];

            // Legge i valori
            const nome = document.getElementById("nome").value.trim();
            const categoriaId = document.getElementById("categoriaId").value;
            const prezzo = parseFloat(document.getElementById("prezzo").value);
            const quantita = parseInt(document.getElementById("quantita").value, 10);
            const immagine = document.getElementById("immagine").value.trim();
            const descrizione = document.getElementById("descrizione").value.trim();

            if (nome === "") {
                errors.push("Il nome del prodotto è obbligatorio.");
            }

            if (categoriaId === "") {
                errors.push("Devi selezionare una categoria.");
            }

            if (isNaN(prezzo) || prezzo <= 0) {
                errors.push("Il prezzo deve essere maggiore di 0.");
            }

            if (isNaN(quantita) || quantita < 0) {
                errors.push("La quantità in magazzino non può essere negativa.");
            }

            if (immagine === "") {
                errors.push("Il percorso dell'immagine è obbligatorio.");
            }

            if (descrizione === "") {
                errors.push("La descrizione è obbligatoria.");
            }

            // Se ci sono errori, blocca l'invio e mostra i messaggi
            if (errors.length > 0) {
                event.preventDefault();
                
                const errorDiv = document.createElement("div");
                errorDiv.className = "js-error-msg";
                errorDiv.style.color = "#e53e3e";
                errorDiv.style.marginBottom = "1rem";
                errorDiv.style.fontSize = "0.9rem";
                errorDiv.style.padding = "10px";
                errorDiv.style.background = "#fed7d7";
                errorDiv.style.borderRadius = "4px";
                
                let ul = document.createElement("ul");
                ul.style.margin = "0";
                ul.style.paddingLeft = "20px";
                
                errors.forEach(err => {
                    let li = document.createElement("li");
                    li.textContent = err;
                    ul.appendChild(li);
                });
                
                errorDiv.appendChild(ul);
                form.insertBefore(errorDiv, form.firstChild);
            }
        });
    }
});
