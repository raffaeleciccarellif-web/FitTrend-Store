document.addEventListener("DOMContentLoaded", function() {
    const forms = document.querySelectorAll(".add-to-cart-form");

    forms.forEach(form => {
        form.addEventListener("submit", function(event) {
            event.preventDefault();

            const url = form.getAttribute("action");
            const formData = new FormData(form);
            const params = new URLSearchParams(formData).toString();
            
            let msgContainer = form.querySelector(".cart-msg-container");
            if (!msgContainer) {
                msgContainer = document.createElement("div");
                msgContainer.className = "cart-msg-container mt-md";
                form.appendChild(msgContainer);
            }
            
            msgContainer.innerHTML = "";

            fetch(url, {
                method: "POST",
                headers: {
                    "Content-Type": "application/x-www-form-urlencoded",
                    "X-Requested-With": "XMLHttpRequest"
                },
                body: params
            })
            .then(response => {
                if (!response.ok) {
                    if (response.status === 400 || response.status === 500) {
                        return response.json().catch(() => {
                            throw new Error("Errore dal server.");
                        });
                    }
                    throw new Error("Errore di rete o server non raggiungibile");
                }
                return response.json();
            })
            .then(data => {
                if (data.success) {
                    msgContainer.innerHTML = `<div class="success-msg">${data.message || "Prodotto aggiunto al carrello!"}</div>`;
                    
                    const cartCountBadge = document.getElementById("cart-count");
                    if (cartCountBadge && data.cartCount !== undefined) {
                        cartCountBadge.textContent = data.cartCount;
                    }
                } else {
                    msgContainer.innerHTML = `<div class="error-msg">${data.error || "Impossibile aggiungere il prodotto."}</div>`;
                }
            })
            .catch(error => {
                console.error("Errore fetch carrello:", error);
                const errorText = error.error || error.message || "Si è verificato un errore inaspettato.";
                msgContainer.innerHTML = `<div class="error-msg">${errorText}</div>`;
            });
        });
    });
});
