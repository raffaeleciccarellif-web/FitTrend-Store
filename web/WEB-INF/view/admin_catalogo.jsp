<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Prodotti - FitTrend Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <style>
        .admin-layout {
            display: grid;
            grid-template-columns: 1fr 3fr;
            gap: 2rem;
            margin-top: 2rem;
        }
        @media (max-width: 768px) {
            .admin-layout { grid-template-columns: 1fr; }
        }
        .form-section { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); }
        .table-section { background: white; padding: 1.5rem; border-radius: 8px; box-shadow: 0 4px 6px rgba(0,0,0,0.05); overflow-x: auto; }
        .form-group { margin-bottom: 1rem; }
        .form-group label { display: block; margin-bottom: 0.5rem; font-weight: bold; }
        .form-group input, .form-group textarea, .form-group select { width: 100%; padding: 0.75rem; border: 1px solid #ccc; border-radius: 4px; }
        .status-badge { padding: 4px 8px; border-radius: 12px; font-size: 0.8rem; font-weight: 600; }
        .status-active { background: #c6f6d5; color: #22543d; }
        .status-deleted { background: #fed7d7; color: #822727; }
        .img-preview { width: 50px; height: 50px; object-fit: cover; border-radius: 4px; }
    </style>
</head>
<body>

    <jsp:include page="/WEB-INF/view/header.jsp" />

    <main class="container">
        <h1>Gestione Prodotti</h1>
        
        <c:if test="${not empty param.error}">
            <div class="error-msg">Errore durante l'operazione: <c:out value="${param.error}" /></div>
        </c:if>
        <c:if test="${not empty param.msg}">
            <div style="background: #c6f6d5; color: #22543d; padding: 1rem; border-radius: 4px; margin-bottom: 1rem;">
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
                        <input type="text" id="nome" name="nome" value="<c:out value="${prodottoInModifica.nome}"/>" required>
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
                        <input type="number" step="0.01" min="0.01" id="prezzo" name="prezzo" value="<c:out value="${prodottoInModifica.prezzo}"/>" required>
                    </div>
                    
                    <div class="form-group">
                        <label for="quantita">Quantità in Magazzino</label>
                        <input type="number" min="0" id="quantita" name="quantita" value="${not empty prodottoInModifica ? prodottoInModifica.quantitaDisponibile : 0}" required>
                    </div>

                    <div class="form-group">
                        <label for="immagine">Percorso Immagine</label>
                        <input type="text" id="immagine" name="immagine" placeholder="es. images/products/file.jpg" value="<c:out value="${prodottoInModifica.immagine}"/>" required>
                    </div>

                    <div class="form-group">
                        <label for="descrizione">Descrizione</label>
                        <textarea id="descrizione" name="descrizione" rows="4" required><c:out value="${prodottoInModifica.descrizione}"/></textarea>
                    </div>

                    <button type="submit" class="btn">${not empty prodottoInModifica ? 'Salva Modifiche' : 'Aggiungi Prodotto'}</button>
                    <c:if test="${not empty prodottoInModifica}">
                        <a href="${pageContext.request.contextPath}/admin/prodotti" class="btn" style="background:#718096; margin-top:0.5rem; display:block; text-align:center;">Annulla</a>
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
                                    <img src="${pageContext.request.contextPath}/${p.immagine}" alt="<c:out value="${p.nome}"/>" class="img-preview">
                                </td>
                                <td>${p.id}</td>
                                <td><strong><c:out value="${p.nome}"/></strong></td>
                                <td><c:out value="${p.categoriaNome}"/></td>
                                <td>€${p.prezzo}</td>
                                <td>${p.quantitaDisponibile}</td>
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
                                    <form action="${pageContext.request.contextPath}/admin/prodotti" method="GET" style="display:inline;">
                                        <input type="hidden" name="action" value="modifica">
                                        <input type="hidden" name="id" value="${p.id}">
                                        <button type="submit" class="btn" style="padding:0.25rem 0.5rem; font-size:0.8rem;">Modifica</button>
                                    </form>
                                    
                                    <c:if test="${not p.deleted}">
                                        <form action="${pageContext.request.contextPath}/admin/prodotti" method="POST" style="display:inline;" onsubmit="return confirm('Vuoi davvero eliminare questo prodotto?');">
                                            <input type="hidden" name="action" value="elimina">
                                            <input type="hidden" name="id" value="${p.id}">
                                            <button type="submit" class="btn" style="background:#e53e3e; padding:0.25rem 0.5rem; font-size:0.8rem;">Elimina</button>
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

    <script src="${pageContext.request.contextPath}/scripts/admin-products-validation.js"></script>
</body>
</html>
