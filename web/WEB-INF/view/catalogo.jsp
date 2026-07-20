<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="prodotti" type="java.util.Collection<model.Prodotto>"--%>
<%--@elvariable id="categorie" type="java.util.Collection<model.Categoria>"--%>
<%--@elvariable id="filtroNome" type="java.lang.String"--%>
<%--@elvariable id="filtroCategoriaId" type="java.lang.String"--%>
<%--@elvariable id="filtroPrezzoMin" type="java.lang.String"--%>
<%--@elvariable id="filtroPrezzoMax" type="java.lang.String"--%>
<%--@elvariable id="filtroOrder" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <meta name="description" content="Esplora il catalogo di FitTrend Store: accessori, abbigliamento e attrezzi per palestra e fitness.">
    <title>Catalogo — FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/catalogo.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main>
    <div class="container">
        <h1 class="page-title">Catalogo Prodotti</h1>

        <%-- ── Barra filtri ───────────────────────────────────────────────────── --%>
        <section class="filtri-bar" aria-label="Filtri di ricerca">
            <form id="filtriForm" method="get" action="${pageContext.request.contextPath}/catalogo">

                <%-- Nome --%>
                <div class="filtro-group">
                    <label for="nome">Cerca</label>
                    <input type="search" id="nome" name="nome"
                           placeholder="Nome prodotto…"
                           value="<c:out value='${filtroNome}'/>">
                </div>

                <%-- Categoria --%>
                <div class="filtro-group">
                    <label for="categoriaId">Categoria</label>
                    <select id="categoriaId" name="categoriaId">
                        <option value="">Tutte le categorie</option>
                        <c:forEach var="categoria" items="${categorie}">
                            <option value="${categoria.id}"
                                <c:if test="${categoria.id == filtroCategoriaId}">selected</c:if>>
                                <c:out value="${categoria.nome}"/>
                            </option>
                        </c:forEach>
                    </select>
                </div>

                <%-- Prezzo min --%>
                <div class="filtro-group">
                    <label for="prezzoMin">Prezzo min (€)</label>
                    <input type="number" id="prezzoMin" name="prezzoMin"
                           min="1" max="10000" step="0.01" placeholder="1,00"
                           value="<c:out value='${filtroPrezzoMin}'/>">
                </div>

                <%-- Prezzo max --%>
                <div class="filtro-group">
                    <label for="prezzoMax">Prezzo max (€)</label>
                    <input type="number" id="prezzoMax" name="prezzoMax"
                           min="1" max="10000" step="0.01" placeholder="10000,00"
                           value="<c:out value='${filtroPrezzoMax}'/>">
                </div>

                <%-- Ordina per --%>
                <div class="filtro-group">
                    <label for="order">Ordina per</label>
                    <select id="order" name="order">
                        <option value="nome"      <c:if test="${filtroOrder == 'nome'}">selected</c:if>>Nome</option>
                        <option value="prezzo"    <c:if test="${filtroOrder == 'prezzo'}">selected</c:if>>Prezzo</option>
                        <option value="categoria" <c:if test="${filtroOrder == 'categoria'}">selected</c:if>>Categoria</option>
                    </select>
                </div>

                <div class="filtro-group filtro-btn">
                    <button type="submit" class="btn" id="btnFiltra">Filtra</button>
                    <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-secondary">Reset</a>
                </div>

            </form>
        </section>

        <%-- ── Griglia prodotti ───────────────────────────────────────────────── --%>
        <c:choose>
            <c:when test="${empty prodotti}">
                <p class="info-msg">Nessun prodotto trovato con i filtri selezionati.</p>
            </c:when>
            <c:otherwise>
                <div class="grid-prodotti" id="gridProdotti">
                    <c:forEach var="prodotto" items="${prodotti}">
                        <article class="card">
                            <a href="<c:url value='/prodotto?id=${prodotto.id}'/>" aria-label="Vedi dettaglio ${prodotto.nome}">
                                <img class="card-img"
                                     src="${pageContext.request.contextPath}/<c:out value='${prodotto.immagine}'/>"
                                     alt="<c:out value='${prodotto.nome}'/>">
                            </a>
                            <div class="card-body">
                                <p class="card-categoria"><c:out value="${prodotto.categoriaNome}"/></p>
                                <h2 class="card-title card-title-clamp" title="${prodotto.nome}"><c:out value="${prodotto.nome}"/></h2>
                                <p class="card-text card-text-clamp"><c:out value="${prodotto.descrizione}"/></p>
                                <div class="card-footer">
                                    <a href="<c:url value='/prodotto?id=${prodotto.id}'/>"
                                       class="btn" id="btnDettaglio-${prodotto.id}">
                                        Vedi dettaglio
                                    </a>
                                    <p class="card-price">€ <c:out value="${prodotto.prezzo}"/></p>
                                </div>
                            </div>
                        </article>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>

    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
