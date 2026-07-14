<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Ordini - FitTrend Store Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <style>
        .badge { display: inline-block; padding: 3px 8px; border-radius: 12px; font-size: 0.8em; font-weight: bold; background: #eee; }
        .badge-consegnato { background: #d4edda; color: #155724; }
        .badge-in_elaborazione { background: #fff3cd; color: #856404; }
        .badge-in_consegna { background: #cce5ff; color: #004085; }
        .badge-annullato { background: #f8d7da; color: #721c24; }
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
        <h1>Gestione Ordini</h1>

        <c:if test="${not empty errore}">
            <div class="error-message" style="color: red; margin-bottom: 15px;"><c:out value="${errore}" /></div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-message" style="color: green; margin-bottom: 15px;"><c:out value="${messaggio}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/ordini" method="get" class="filters">
            <div class="form-group">
                <label for="dataInizio">Data Inizio:</label>
                <input type="date" id="dataInizio" name="dataInizio" value="<c:out value='${param.dataInizio}'/>" style="padding: 5px;">
            </div>
            <div class="form-group">
                <label for="dataFine">Data Fine:</label>
                <input type="date" id="dataFine" name="dataFine" value="<c:out value='${param.dataFine}'/>" style="padding: 5px;">
            </div>
            <div class="form-group">
                <label for="utenteId">ID Utente:</label>
                <input type="number" id="utenteId" name="utenteId" value="<c:out value='${param.utenteId}'/>" style="width: 100px; padding: 5px;">
            </div>
            <div class="form-group">
                <label for="stato">Stato:</label>
                <select id="stato" name="stato" style="padding: 5px;">
                    <option value="">Tutti</option>
                    <option value="in_elaborazione" <c:if test="${param.stato == 'in_elaborazione'}">selected</c:if>>In Elaborazione</option>
                    <option value="in_consegna" <c:if test="${param.stato == 'in_consegna'}">selected</c:if>>In Consegna</option>
                    <option value="consegnato" <c:if test="${param.stato == 'consegnato'}">selected</c:if>>Consegnato</option>
                    <option value="annullato" <c:if test="${param.stato == 'annullato'}">selected</c:if>>Annullato</option>
                </select>
            </div>
            <button type="submit" class="btn btn-primary" style="padding: 5px 15px;">Filtra</button>
            <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-secondary" style="padding: 5px 15px; text-decoration: none;">Reset</a>
        </form>

        <table>
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Data</th>
                    <th>ID Utente</th>
                    <th>Totale</th>
                    <th>Stato</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:choose>
                    <c:when test="${empty ordini}">
                        <tr><td colspan="6">Nessun ordine trovato con i filtri selezionati.</td></tr>
                    </c:when>
                    <c:otherwise>
                        <c:forEach var="ordine" items="${ordini}">
                            <tr>
                                <td><c:out value="${ordine.id}" /></td>
                                <td><c:out value="${ordine.dataOrdine}" /></td>
                                <td><c:out value="${ordine.utenteId}" /></td>
                                <td>&euro; <c:out value="${ordine.totale}" /></td>
                                <td>
                                    <span class="badge badge-${ordine.stato}">
                                        <c:out value="${ordine.statoLabel}" />
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${ordine.stato == 'in_elaborazione' || ordine.stato == 'in_consegna'}">
                                        <form action="${pageContext.request.contextPath}/admin/ordini" method="post" style="display: flex; gap: 5px; align-items: center; margin: 0;">
                                            <input type="hidden" name="action" value="aggiornaStato">
                                            <input type="hidden" name="ordineId" value="${ordine.id}">
                                            <select name="nuovoStato" required style="padding: 5px;">
                                                <option value="">Cambia in...</option>
                                                <c:if test="${ordine.stato == 'in_elaborazione'}">
                                                    <option value="in_consegna">In Consegna</option>
                                                    <option value="annullato">Annullato</option>
                                                </c:if>
                                                <c:if test="${ordine.stato == 'in_consegna'}">
                                                    <option value="consegnato">Consegnato</option>
                                                    <option value="annullato">Annullato</option>
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
