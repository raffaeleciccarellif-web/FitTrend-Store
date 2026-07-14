<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <style>
        .error-text { color: red; font-size: 0.9em; display: block; margin-top: 5px; }
        .form-group { margin-bottom: 15px; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="checkout-container" style="max-width: 600px; margin: 0 auto; padding: 20px;">
        <h1>Checkout</h1>

        <c:if test="${not empty errore}">
            <div class="error-message" style="color: red; margin-bottom: 15px;">
                <c:out value="${errore}" />
            </div>
        </c:if>

        <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="post">
            <div class="form-group">
                <label for="indirizzoSpedizione">Indirizzo di Spedizione:</label>
                <input type="text" id="indirizzoSpedizione" name="indirizzoSpedizione" required style="width: 100%; padding: 8px;">
                <span class="error-text" id="indirizzoSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="cittaSpedizione">Città:</label>
                <input type="text" id="cittaSpedizione" name="cittaSpedizione" required style="width: 100%; padding: 8px;">
                <span class="error-text" id="cittaSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="capSpedizione">CAP:</label>
                <input type="text" id="capSpedizione" name="capSpedizione" required style="width: 100%; padding: 8px;">
                <span class="error-text" id="capSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="metodoPagamento">Metodo di Pagamento:</label>
                <select id="metodoPagamento" name="metodoPagamento" required style="width: 100%; padding: 8px;">
                    <option value="">Seleziona...</option>
                    <option value="carta">Carta di Credito / Debito</option>
                    <option value="paypal">PayPal</option>
                    <option value="bonifico">Bonifico Bancario</option>
                </select>
                <span class="error-text" id="metodoPagamentoError"></span>
            </div>

            <div class="form-group" id="cartaContainer" style="display: none;">
                <label for="numeroCarta">Numero Carta (simulato):</label>
                <input type="text" id="numeroCarta" name="numeroCarta" style="width: 100%; padding: 8px;">
                <span class="error-text" id="numeroCartaError"></span>
            </div>

            <button type="submit" class="btn btn-primary" style="padding: 10px 20px;">Conferma Ordine</button>
        </form>
    </main>

    <jsp:include page="footer.jsp" />

    <script src="${pageContext.request.contextPath}/scripts/checkout-validation.js"></script>
</body>
</html>
