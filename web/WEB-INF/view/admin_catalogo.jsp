<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="prodottoInModifica" type="model.Prodotto"--%>
<%--@elvariable id="categorie" type="java.util.Collection<model.Categoria>"--%>
<%--@elvariable id="prodotti" type="java.util.Collection<model.Prodotto>"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Prodotti - FitTrend Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}../styles/main.css">

</head>
<body>

    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>Gestione Prodotti</h1>
        
        <c:if test="${not empty param.error}">
            <div class="error-msg">Errore durante l'operazione: <c:out value="${param.error}" /></div>
        </c:if>
        <c:if test="${not empty param.msg}">
            <div class="success-msg">
                <c:out value="${param.msg}" />
            </div>
        </c:if>

        <div class="admin-layout">
            <!-- Colonna Sinistra: Form Aggiungi/Modifica -->
            <section class="form-section">
                <h2>${not empty prodottoInModifica ? 'Modifica Prodotto' : 'Nuovo Prodotto'}</h2>
                <form id="adminProductForm" action="${pageContext.request.contextPath}/admin/prodotti" method="POST">
                    <input type="hidden" name="action" value="salva">
                    <input type="hidden" name="id" value="${prodottoInModifica.id}">
                    
                    <div class="form-group">
                        <label for="nome">Nome Prodotto</label>
                        <input type="text" id="nome" name="nome" value="<c:out value='${prodottoInModifica.nome}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="categoriaId">Categoria</label>
                        <select id="categoriaId" name="categoriaId" required>
                            <option value="">-- Seleziona Categoria --</option>
                            <c:forEach var="cat" items="${categorie}">
                                <option value="${cat.id}" ${cat.id == prodottoInModifica.categoriaId ? 'selected' : ''}>
                                    <c:out value="${cat.nome}"/>
                                </option>
                            </c:forEach>
                        </select>
                    </div>
                    
                    <div class="form-group">
                        <label for="prezzo">Prezzo (€)</label>
                        <input type="number" step="0.01" min="0.01" id="prezzo" name="prezzo" value="<c:out value='${prodottoInModifica.prezzo}'/>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="quantita">Quantità in Magazzino</label>
                        <input type="number" min="0" id="quantita" name="quantita" value="${not empty prodottoInModifica ? prodottoInModifica.quantitaDisponibile : 0}" required>
                    </div>

                    <div class="form-group">
                        <label for="immagine">Percorso Immagine</label>
                        <input type="text" id="immagine" name="immagine" placeholder="es. images/products/file.jpg" value="<c:out value='${prodottoInModifica.immagine}'/>" required>
                    </div>

                    <div class="form-group">
                        <label for="descrizione">Descrizione</label>
                        <textarea id="descrizione" name="descrizione" rows="4" required><c:out value="${prodottoInModifica.descrizione}"/></textarea>
                    </div>

                    <button type="submit" class="btn">${not empty prodottoInModifica ? 'Salva Modifiche' : 'Aggiungi Prodotto'}</button>
                    <c:if test="${not empty prodottoInModifica}">
                        <a href="${pageContext.request.contextPath}/admin/prodotti" class="btn btn-secondary mt-md text-center d-block">Annulla</a>
                    </c:if>
                </form>
            </section>

            <!-- Colonna Destra: Tabella Prodotti -->
            <section class="table-section">
                <h2>Catalogo Completo</h2>
                <table class="table-responsive">
                    <thead>
                        <tr>
                            <th>Img</th>
                            <th>ID</th>
                            <th>Nome</th>
                            <th>Categoria</th>
                            <th>Prezzo</th>
                            <th>Stock</th>
                            <th>Stato</th>
                            <th>Azioni</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="p" items="${prodotti}">
                            <tr>
                                <td>
                                    <img src="${pageContext.request.contextPath}/${p.immagine}" alt="<c:out value='${p.nome}'/>" class="img-preview">
                                </td>
                                <td><c:out value="${p.id}"/></td>
                                <td><strong><c:out value="${p.nome}"/></strong></td>
                                <td><c:out value="${p.categoriaNome}"/></td>
                                <td>€<c:out value="${p.prezzo}"/></td>
                                <td><c:out value="${p.quantitaDisponibile}"/></td>
                                <td>
                                    <c:choose>
                                        <c:when test="${p.deleted}">
                                            <span class="status-badge status-deleted">Eliminato</span>
                                        </c:when>
                                        <c:otherwise>
                                            <span class="status-badge status-active">Attivo</span>
                                        </c:otherwise>
                                    </c:choose>
                                </td>
                                <td>
                                    <form action="${pageContext.request.contextPath}/admin/prodotti" method="GET" class="inline-form">
                                        <input type="hidden" name="action" value="modifica">
                                        <input type="hidden" name="id" value="${p.id}">
                                        <button type="submit" class="btn btn-sm">Modifica</button>
                                    </form>
                                    
                                    <c:if test="${not p.deleted}">
                                        <form action="${pageContext.request.contextPath}/admin/prodotti" method="POST" class="inline-form" onsubmit="return confirm('Vuoi davvero eliminare questo prodotto?');">
                                            <input type="hidden" name="action" value="elimina">
                                            <input type="hidden" name="id" value="${p.id}">
                                            <button type="submit" class="btn btn-danger btn-sm">Elimina</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
            </section>
        </div>
    </main>

    <script src="<c:url value='/scripts/admin-products-validation.js'/>" defer></script>
</body>
</html>
