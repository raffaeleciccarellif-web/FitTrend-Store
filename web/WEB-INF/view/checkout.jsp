<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>Checkout</h1>

        <c:if test="${not empty errore}">
            <div class="error-msg">
                <c:out value="${errore}" />
            </div>
        </c:if>

        <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="post">
            <div class="form-group">
                <label for="indirizzoSpedizione">Indirizzo di Spedizione:</label>
                <input type="text" id="indirizzoSpedizione" name="indirizzoSpedizione" required>
                <span class="error-msg" id="indirizzoSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="cittaSpedizione">Città:</label>
                <input type="text" id="cittaSpedizione" name="cittaSpedizione" required>
                <span class="error-msg" id="cittaSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="capSpedizione">CAP:</label>
                <input type="text" id="capSpedizione" name="capSpedizione" required>
                <span class="error-msg" id="capSpedizioneError"></span>
            </div>

            <div class="form-group">
                <label for="metodoPagamento">Metodo di Pagamento:</label>
                <select id="metodoPagamento" name="metodoPagamento" required>
                    <option value="">Seleziona...</option>
                    <option value="carta">Carta di Credito / Debito</option>
                    <option value="paypal">PayPal</option>
                    <option value="bonifico">Bonifico Bancario</option>
                </select>
                <span class="error-msg" id="metodoPagamentoError"></span>
            </div>

            <div class="form-group d-none" id="cartaContainer">
                <label for="numeroCarta">Numero Carta (simulato):</label>
                <input type="text" id="numeroCarta" name="numeroCarta">
                <span class="error-msg" id="numeroCartaError"></span>
            </div>

            <button type="submit" class="btn">Conferma Ordine</button>
        </form>
    </main>

    <jsp:include page="footer.jsp" />

    <script src="${pageContext.request.contextPath}/scripts/checkout-validation.js"></script>
</body>
</html>
