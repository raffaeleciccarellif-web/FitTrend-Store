<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="prodotto" type="model.Prodotto"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title><c:out value="${prodotto.nome}" /> - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <script src="${pageContext.request.contextPath}/scripts/cart.js" defer></script>
</head>
<body>
    <header>
        <div class="container">
            <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
            <nav>
                <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
                <c:if test="${not empty sessionScope.utenteLoggato and not sessionScope.utenteLoggato.admin}">
                    <a href="${pageContext.request.contextPath}/carrello?action=visualizza">Carrello</a>
                </c:if>
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
            <nav class="breadcrumb" aria-label="Breadcrumb">
                <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
                <span class="breadcrumb-sep">›</span>
                <a href="${pageContext.request.contextPath}/catalogo?categoriaId=${prodotto.categoriaId}"><c:out value="${prodotto.categoriaNome}"/></a>
                <span class="breadcrumb-sep">›</span>
                <span class="breadcrumb-current"><c:out value="${prodotto.nome}"/></span>
            </nav>

            <div class="card">
                <div class="card-body">
                    <img src="${pageContext.request.contextPath}/${prodotto.immagine}" alt="<c:out value='${prodotto.nome}' />" class="card-img" />
                    
                    <h1 class="page-title mt-md"><c:out value="${prodotto.nome}" /></h1>
                    
                    <p class="card-text"><strong>Categoria:</strong> <c:out value="${prodotto.categoriaNome}" /></p>
                    <p class="card-price">€ <c:out value="${prodotto.prezzo}" /></p>
                    <p class="card-text"><strong>Disponibilità:</strong> <c:out value="${prodotto.quantitaDisponibile}" /> pezzi in stock</p>
                    <p class="card-text mt-md"><c:out value="${prodotto.descrizione}" /></p>

                    <c:if test="${not empty sessionScope.utenteLoggato and not sessionScope.utenteLoggato.admin}">
                        <div class="mt-md">
                            <c:choose>
                                <c:when test="${prodotto.quantitaDisponibile > 0}">
                                    <form action="${pageContext.request.contextPath}/carrello" method="POST" id="addToCartForm" class="add-to-cart-form">
                                        <input type="hidden" name="action" value="aggiungi">
                                        <input type="hidden" name="idProdotto" value="${prodotto.id}">
                                        
                                        <div class="mb-md">
                                            <label for="quantita">Quantità:</label>
                                            <input type="number" id="quantita" name="quantita" value="1" min="1" max="${prodotto.quantitaDisponibile}">
                                            <span class="field-hint">Max disponibili: <c:out value="${prodotto.quantitaDisponibile}"/> pezzi</span>
                                        </div>
                                        
                                        <button type="submit" class="btn">Aggiungi al Carrello</button>
                                    </form>
                                </c:when>
                                <c:otherwise>
                                    <button class="btn btn-secondary" disabled>Esaurito</button>
                                </c:otherwise>
                            </c:choose>
                        </div>
                    </c:if>
                </div>
            </div>
        </div>
    </main>

    <footer>
        <div class="container">
            <p>&copy; 2026 FitTrend Store &mdash; Progetto TSW</p>
        </div>
    </footer>
</body>
</html>
