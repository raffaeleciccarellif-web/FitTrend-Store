<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Rimborsi - FitTrend Store Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <style>
        .badge { display: inline-block; padding: 3px 8px; border-radius: 12px; font-size: 0.8em; font-weight: bold; background: #eee; }
        .badge-completato { background: #d4edda; color: #155724; }
        .badge-richiesto { background: #fff3cd; color: #856404; }
        .badge-approvato { background: #cce5ff; color: #004085; }
        .badge-rifiutato { background: #f8d7da; color: #721c24; }
        table { width: 100%; border-collapse: collapse; margin-top: 20px; }
        table, th, td { border: 1px solid #ddd; }
        th, td { padding: 10px; text-align: left; }
        .filters { display: flex; gap: 10px; margin-bottom: 20px; align-items: flex-end; }
        .filters .form-group { margin-bottom: 0; }
        .admin-container { max-width: 1200px; margin: 0 auto; padding: 20px; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="admin-container">
        <h1>Gestione Rimborsi</h1>

        <c:if test="${not empty errore}">
            <div class="error-message" style="color: red; margin-bottom: 15px;"><c:out value="${errore}" /></div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-message" style="color: green; margin-bottom: 15px;"><c:out value="${messaggio}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/rimborsi" method="get" class="filters">
            <div class="form-group">
                <label for="stato">Stato:</label>
                <select id="stato" name="stato" style="padding: 5px;">
                    <option value="">Tutti</option>
                    <option value="richiesto" <c:if test="${param.stato == 'richiesto'}">selected</c:if>>Richiesto</option>
                    <option value="approvato" <c:if test="${param.stato == 'approvato'}">selected</c:if>>Approvato</option>
                    <option value="rifiutato" <c:if test="${param.stato == 'rifiutato'}">selected</c:if>>Rifiutato</option>
                    <option value="completato" <c:if test="${param.stato == 'completato'}">selected</c:if>>Completato</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="padding: 5px 15px;">Filtra</button>
            <a href="${pageContext.request.contextPath}/admin/rimborsi" class="btn btn-secondary" style="padding: 5px 15px; text-decoration: none;">Reset</a>
        </form>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Ordine ID</th>
                    <th>Utente Email</th>
                    <th>Importo</th>
                    <th>Motivo</th>
                    <th>Data Richiesta</th>
                    <th>Data Elaborazione</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty rimborsi}">
                        <tr><td colspan="9">Nessun rimborso trovato con i filtri selezionati.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="rimborso" items="${rimborsi}">
                            <tr>
                                <td><c:out value="${rimborso.id}" /></td>
                                <td><c:out value="${rimborso.ordineId}" /></td>
                                <td><c:out value="${rimborso.utenteEmail}" /></td>
                                <td>&euro; <c:out value="${rimborso.importo}" /></td>
                                <td><c:out value="${rimborso.motivo}" /></td>
                                <td><c:out value="${rimborso.dataRichiesta}" /></td>
                                <td><c:out value="${empty rimborso.dataElaborazione ? '-' : rimborso.dataElaborazione}" /></td>
                                <td>
                                    <span class="badge badge-${rimborso.stato}">
                                        <c:out value="${rimborso.statoLabel}" />
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${rimborso.stato == 'richiesto' || rimborso.stato == 'approvato'}">
                                        <form action="${pageContext.request.contextPath}/admin/rimborsi" method="post" style="display: flex; gap: 5px; align-items: center; margin: 0;">
                                            <input type="hidden" name="action" value="aggiornaStato">
                                            <input type="hidden" name="idRimborso" value="${rimborso.id}">
                                            <select name="nuovoStato" required style="padding: 5px;">
                                                <option value="">Cambia in...</option>
                                                <c:if test="${rimborso.stato == 'richiesto'}">
                                                    <option value="approvato">Approvato</option>
                                                    <option value="rifiutato">Rifiutato</option>
                                                </c:if>
                                                <c:if test="${rimborso.stato == 'approvato'}">
                                                    <option value="completato">Completato</option>
                                                </c:if>
                                            </select>
                                            <button type="submit" class="btn btn-secondary" style="padding: 5px 10px;">Applica</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>
