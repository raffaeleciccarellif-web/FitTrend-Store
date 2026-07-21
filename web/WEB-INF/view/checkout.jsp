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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/checkout.css">
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
