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
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">

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

        <!-- Sezione Form (Top) -->
        <div class="card mb-lg">
            <div class="card-body">
                <h2 class="mb-md">${not empty prodottoInModifica ? 'Modifica Prodotto' : 'Aggiungi Nuovo Prodotto'}</h2>
                
                <form id="adminProductForm" action="${pageContext.request.contextPath}/admin/prodotti" method="POST">
                    <input type="hidden" name="action" value="salva">
                    <input type="hidden" name="id" value="${prodottoInModifica.id}">
                    
                    <!-- Semplice layout a due colonne per il form usando Flexbox -->
                    <div style="display: flex; flex-wrap: wrap; gap: var(--spacing-md);">
                        <div class="form-group" style="flex: 1; min-width: 250px;">
                            <label for="nome">Nome Prodotto</label>
                            <input type="text" id="nome" name="nome" value="<c:out value='${prodottoInModifica.nome}'/>" required>
                        </div>
    
                        <div class="form-group" style="flex: 1; min-width: 250px;">
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
                    </div>

                    <div style="display: flex; flex-wrap: wrap; gap: var(--spacing-md);">
                        <div class="form-group" style="flex: 1; min-width: 250px;">
                            <label for="prezzo">Prezzo (€)</label>
                            <input type="number" step="0.01" min="0.01" id="prezzo" name="prezzo" value="<c:out value='${prodottoInModifica.prezzo}'/>" required>
                        </div>
                        
                        <div class="form-group" style="flex: 1; min-width: 250px;">
                            <label for="quantita">Quantità in Magazzino</label>
                            <input type="number" min="0" id="quantita" name="quantita" value="${not empty prodottoInModifica ? prodottoInModifica.quantitaDisponibile : 0}" required>
                        </div>
                    </div>
    
                    <div class="form-group">
                        <label for="immagine">Percorso Immagine</label>
                        <input type="text" id="immagine" name="immagine" placeholder="es. images/products/file.jpg" value="<c:out value='${prodottoInModifica.immagine}'/>" required>
                    </div>
    
                    <div class="form-group">
                        <label for="descrizione">Descrizione</label>
                        <textarea id="descrizione" name="descrizione" rows="4" required><c:out value="${prodottoInModifica.descrizione}"/></textarea>
                    </div>

                    <div class="admin-filters-actions">
                        <c:if test="${not empty prodottoInModifica}">
                            <a href="${pageContext.request.contextPath}/admin/prodotti" class="btn btn-secondary">Annulla</a>
                        </c:if>
                        <button type="submit" class="btn">
                            ${not empty prodottoInModifica ? 'Salva Modifiche' : 'Salva Prodotto'}
                        </button>
                    </div>
                </form>
            </div>
        </div>

        <!-- Sezione Tabella (Bottom) -->
        <div class="card">
            <div class="card-body">
                <h2 class="mb-md">Catalogo Prodotti</h2>
                
                <div class="table-responsive">
                    <table>
                        <thead>
                            <tr>
                                <th>Img</th>
                                <th>ID</th>
                                <th>Nome Prodotto</th>
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
                                    <td>€ <c:out value="${p.prezzo}"/></td>
                                    <td><c:out value="${p.quantitaDisponibile}"/> pz</td>
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
                                            <button type="submit" class="btn btn-sm btn-secondary">Modifica</button>
                                        </form>
                                        
                                        <c:if test="${not p.deleted}">
                                            <form action="${pageContext.request.contextPath}/admin/prodotti" method="POST" class="inline-form" onsubmit="return confirm('Vuoi davvero eliminare questo prodotto?');">
                                                <input type="hidden" name="action" value="elimina">
                                                <input type="hidden" name="id" value="${p.id}">
                                                <button type="submit" class="btn btn-sm btn-danger">Elimina</button>
                                            </form>
                                        </c:if>
                                    </td>
                                </tr>
                            </c:forEach>
                        </tbody>
                    </table>
                </div>
            </div>


    </main>

    <script src="<c:url value='/scripts/admin-products-validation.js'/>" defer></script>
</body>
</html>
