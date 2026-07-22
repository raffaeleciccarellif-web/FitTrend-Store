<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="rimborsi" type="java.util.Collection<model.Rimborso>"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Rimborsi - FitTrend Store Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>Gestione Rimborsi</h1>

        <c:if test="${not empty errore}">
            <div class="error-msg"><c:out value="${errore}" /></div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-msg"><c:out value="${messaggio}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/rimborsi" method="get" class="filters">
            <div class="form-group">
                <label for="stato">Stato:</label>
                <select id="stato" name="stato">
                    <option value="">Tutti</option>
                    <option value="richiesto" <c:if test="${param.stato == 'richiesto'}">selected</c:if>>Richiesto</option>
                    <option value="approvato" <c:if test="${param.stato == 'approvato'}">selected</c:if>>Approvato</option>
                    <option value="rifiutato" <c:if test="${param.stato == 'rifiutato'}">selected</c:if>>Rifiutato</option>
                    <option value="completato" <c:if test="${param.stato == 'completato'}">selected</c:if>>Completato</option>
                </select>
            </div>
            <button type="submit" class="btn">Filtra</button>
            <a href="${pageContext.request.contextPath}/admin/rimborsi" class="btn btn-secondary">Reset</a>
        </form>

        <div class="table-responsive" style="min-width: 0; overflow-x: auto;">
            <table style="min-width: 900px;">
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
                                <td class="td-motivo" title="<c:out value='${rimborso.motivo}'/>"><c:out value="${rimborso.motivo}" /></td>
                                <td><c:out value="${rimborso.dataRichiesta}" /></td>
                                <td><c:out value="${empty rimborso.dataElaborazione ? '-' : rimborso.dataElaborazione}" /></td>
                                <td>
                                    <span class="status-badge status-badge-${rimborso.stato}">
                                        <c:out value="${rimborso.statoLabel}" />
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${rimborso.stato == 'richiesto' || rimborso.stato == 'approvato'}">
                                        <form action="${pageContext.request.contextPath}/admin/rimborsi" method="post"
                                              style="display:flex;align-items:center;gap:6px;">
                                            <input type="hidden" name="action" value="aggiornaStato">
                                            <input type="hidden" name="idRimborso" value="${rimborso.id}">
                                            <select name="nuovoStato" required
                                                    style="min-width:155px;width:155px;padding:4px 6px;font-size:0.85rem;">
                                                <option value="">Cambia in...</option>
                                                <c:if test="${rimborso.stato == 'richiesto'}">
                                                    <option value="approvato">Approvato</option>
                                                    <option value="rifiutato">Rifiutato</option>
                                                </c:if>
                                                <c:if test="${rimborso.stato == 'approvato'}">
                                                    <option value="completato">Completato</option>
                                                </c:if>
                                            </select>
                                            <button type="submit" class="btn btn-sm btn-secondary">Applica</button>
                                        </form>
                                    </c:if>
                                </td>
                            </tr>
                        </c:forEach>
                    </c:otherwise>
                </c:choose>
            </tbody>
        </table>
        </div>
        
        <%-- Componente di Paginazione --%>
        <jsp:include page="paginazione.jsp" />
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>
