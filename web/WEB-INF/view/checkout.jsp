<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="errore" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout - FitTrend Store</title>
    <meta name="description" content="Completa il tuo acquisto su FitTrend Store.">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <style>
        .checkout-card {
            max-width: 560px;
            margin: 0 auto;
            background-color: var(--color-surface);
            border: 1px solid var(--color-border);
            border-radius: var(--radius-lg);
            padding: var(--spacing-xl);
            box-shadow: var(--shadow-md);
        }

        .checkout-card h1 {
            font-size: var(--font-size-xl);
            font-weight: 700;
            color: var(--color-text);
            margin-bottom: var(--spacing-xl);
            padding-bottom: var(--spacing-md);
            border-bottom: 1px solid var(--color-border);
        }

        .section-label {
            font-size: 0.75rem;
            font-weight: 700;
            text-transform: uppercase;
            letter-spacing: 0.08em;
            color: var(--color-primary);
            margin-bottom: var(--spacing-md);
            margin-top: var(--spacing-lg);
        }

        .section-label:first-of-type {
            margin-top: 0;
        }

        .form-row {
            display: grid;
            grid-template-columns: 1fr 1fr;
            gap: var(--spacing-md);
        }

        @media (max-width: 520px) {
            .form-row { grid-template-columns: 1fr; }
            .checkout-card { padding: var(--spacing-lg); }
        }

        .divider {
            border: none;
            border-top: 1px solid var(--color-border);
            margin: var(--spacing-lg) 0;
        }

        .btn-checkout {
            width: 100%;
            margin-top: var(--spacing-lg);
            padding: 0.8rem;
            font-size: 1rem;
            font-weight: 700;
        }

        .back-link {
            display: inline-flex;
            align-items: center;
            gap: 4px;
            font-size: var(--font-size-sm);
            color: var(--color-text-light);
            margin-bottom: var(--spacing-lg);
            transition: color var(--transition-fast);
        }
        .back-link:hover { color: var(--color-primary); }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <main>
        <div class="container">

            <a href="${pageContext.request.contextPath}/carrello?action=visualizza" class="back-link">
                &larr; Torna al carrello
            </a>

            <div class="checkout-card">
                <h1>Checkout</h1>

                <c:if test="${not empty errore}">
                    <div class="error-msg" role="alert">
                        <c:out value="${errore}" />
                    </div>
                </c:if>

                <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="post" novalidate>

                    <p class="section-label">Spedizione</p>

                    <div class="form-group">
                        <label for="indirizzoSpedizione" class="label-required">Indirizzo</label>
                        <input type="text"
                               id="indirizzoSpedizione"
                               name="indirizzoSpedizione"
                               placeholder="Es. Via Roma 42"
                               required
                               autocomplete="street-address">
                        <span class="error-msg" id="indirizzoSpedizioneError"></span>
                    </div>

                    <div class="form-row">
                        <div class="form-group">
                            <label for="cittaSpedizione" class="label-required">Città</label>
                            <input type="text"
                                   id="cittaSpedizione"
                                   name="cittaSpedizione"
                                   placeholder="Es. Milano"
                                   required
                                   autocomplete="address-level2">
                            <span class="error-msg" id="cittaSpedizioneError"></span>
                        </div>

                        <div class="form-group">
                            <label for="capSpedizione" class="label-required">CAP</label>
                            <input type="text"
                                   id="capSpedizione"
                                   name="capSpedizione"
                                   placeholder="Es. 20100"
                                   required
                                   autocomplete="postal-code"
                                   maxlength="5"
                                   inputmode="numeric">
                            <span class="error-msg" id="capSpedizioneError"></span>
                        </div>
                    </div>

                    <hr class="divider">

                    <p class="section-label">Pagamento</p>

                    <div class="form-group">
                        <label for="metodoPagamento" class="label-required">Metodo di Pagamento</label>
                        <select id="metodoPagamento" name="metodoPagamento" required>
                            <option value="">Seleziona...</option>
                            <option value="carta">Carta di Credito / Debito</option>
                            <option value="paypal">PayPal</option>
                            <option value="bonifico">Bonifico Bancario</option>
                        </select>
                        <span class="error-msg" id="metodoPagamentoError"></span>
                    </div>

                    <div class="form-group d-none" id="cartaContainer">
                        <label for="numeroCarta">Numero Carta (simulato)</label>
                        <input type="text"
                               id="numeroCarta"
                               name="numeroCarta"
                               placeholder="•••• •••• •••• ••••"
                               maxlength="19"
                               inputmode="numeric">
                        <span class="error-msg" id="numeroCartaError"></span>
                        <span class="field-hint">Solo a scopo dimostrativo.</span>
                    </div>

                    <button type="submit" class="btn btn-checkout">Conferma Ordine</button>
                </form>
            </div>

        </div>
    </main>

    <jsp:include page="footer.jsp" />

    <script src="${pageContext.request.contextPath}/scripts/checkout-validation.js"></script>
</body>
</html>
