<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="carrello" type="model.Carrello"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Carrello - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/carrello.css">
    <script src="${pageContext.request.contextPath}../scripts/cart.js" defer></script>
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
            <nav>
                <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
                <a href="${pageContext.request.contextPath}/carrello?action=visualizza" class="active">Carrello</a>
                <c:choose>
                    <c:when test="${not empty sessionScope.utenteLoggato}">
                        <c:choose>
                            <c:when test="${sessionScope.utenteLoggato.admin}">
                                <a href="${pageContext.request.contextPath}/admin/prodotti">Admin Prodotti</a>
                                <a href="${pageContext.request.contextPath}/admin/ordini">Admin Ordini</a>
                                <a href="${pageContext.request.contextPath}/admin/rimborsi">Admin Rimborsi</a>
                            </c:when>
                            <c:otherwise>
                                <a href="${pageContext.request.contextPath}/ordini">I Miei Ordini</a>
                                <a href="${pageContext.request.contextPath}/rimborsi">I Miei Rimborsi</a>
                            </c:otherwise>
                        </c:choose>
                        <a href="${pageContext.request.contextPath}/logout">Esci</a>
                    </c:when>
                    <c:otherwise>
                        <a href="${pageContext.request.contextPath}/login">Accedi</a>
                        <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
                    </c:otherwise>
                </c:choose>
            </nav>
        </div>
    </header>

    <main>
        <div class="container">
            <h1 class="page-title">Il tuo Carrello</h1>

            <c:if test="${not empty errore}">
                <div class="error-msg"><c:out value="${errore}" /></div>
            </c:if>
            <c:if test="${not empty messaggio}">
                <div class="success-msg"><c:out value="${messaggio}" /></div>
            </c:if>

            <c:choose>
                <c:when test="${empty carrello or empty carrello.items}">
                    <div class="cart-vuoto">
                        <p>Il tuo carrello è vuoto.</p>
                        <a href="${pageContext.request.contextPath}/catalogo" class="btn">Vai al Catalogo</a>
                    </div>
                </c:when>
                <c:otherwise>
                    <div class="table-responsive">
                        <table class="cart-table">
                            <thead>
                                <tr>
                                    <th>Immagine</th>
                                    <th>Prodotto</th>
                                    <th>Prezzo Unitario</th>
                                    <th>Quantità</th>
                                    <th>Subtotale</th>
                                    <th>Azioni</th>
                                </tr>
                            </thead>
                            <tbody>
                                <c:forEach var="item" items="${carrello.items}">
                                    <tr>
                                        <td>
                                            <img src="${pageContext.request.contextPath}/${item.immagine}" alt="<c:out value='${item.nome}' />" class="cart-img" />
                                        </td>
                                        <td>
                                            <span class="cart-product-name"><c:out value="${item.nome}" /></span>
                                        </td>
                                        <td class="cart-price">€ <c:out value="${item.prezzo}" /></td>
                                        <td>
                                            <form action="${pageContext.request.contextPath}/carrello" method="POST" class="form-quantita">
                                                <input type="hidden" name="action" value="modifica">
                                                <input type="hidden" name="idProdotto" value="${item.id}">
                                                <label>
                                                    <input type="number" name="quantita" value="${item.quantita}"
                                                           min="1" max="${item.quantitaDisponibile}"
                                                           required>
                                                </label>
                                                <button type="submit" class="btn btn-aggiorna btn-sm">Aggiorna</button>
                                            </form>
                                        </td>
                                        <td class="cart-subtotale">€ <c:out value="${item.subtotale}" /></td>
                                        <td class="cart-actions">
                                            <form action="${pageContext.request.contextPath}/carrello" method="POST">
                                                <input type="hidden" name="action" value="rimuovi">
                                                <input type="hidden" name="idProdotto" value="${item.id}">
                                                <button type="submit" class="btn btn-danger btn-sm">Rimuovi</button>
                                            </form>
                                        </td>
                                    </tr>
                                </c:forEach>
                            </tbody>
                        </table>
                    </div>

                    <div class="cart-totale-box">
                        <span class="cart-totale-importo">Totale Carrello: € <c:out value="${carrello.totale}" /></span>
                        <div class="cart-totale-azioni">
                            <form action="${pageContext.request.contextPath}/carrello" method="POST">
                                <input type="hidden" name="action" value="svuota">
                                <button type="submit" class="btn btn-secondary">Svuota Carrello</button>
                            </form>
                            <a href="${pageContext.request.contextPath}/checkout" class="btn">Procedi al Checkout</a>
                        </div>
                    </div>
                </c:otherwise>
            </c:choose>
        </div>
    </main>

    <footer>
        <div class="container">
            <p>&copy; 2026 FitTrend Store &mdash; Progetto TSW</p>
        </div>
    </footer>
</body>
</html>
