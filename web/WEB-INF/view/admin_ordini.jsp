<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="ordini" type="java.util.Collection<model.Ordine>"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione Ordini - FitTrend Store Admin</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>Gestione Ordini</h1>

        <c:if test="${not empty errore}">
            <div class="error-msg"><c:out value="${errore}" /></div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-msg"><c:out value="${messaggio}" /></div>
        </c:if>

        <form action="${pageContext.request.contextPath}/admin/ordini" method="get" class="filters">
            <div class="form-group">
                <label for="dataInizio">Data Inizio:</label>
                <input type="date" id="dataInizio" name="dataInizio" value="<c:out value='${param.dataInizio}'/>">
            </div>
            <div class="form-group">
                <label for="dataFine">Data Fine:</label>
                <input type="date" id="dataFine" name="dataFine" value="<c:out value='${param.dataFine}'/>">
            </div>
            <div class="form-group">
                <label for="utenteId">ID Utente:</label>
                <input type="number" id="utenteId" name="utenteId" value="<c:out value='${param.utenteId}'/>">
            </div>
            <div class="form-group">
                <label for="stato">Stato:</label>
                <select id="stato" name="stato">
                    <option value="">Tutti</option>
                    <option value="in_elaborazione" <c:if test="${param.stato == 'in_elaborazione'}">selected</c:if>>In Elaborazione</option>
                    <option value="in_consegna" <c:if test="${param.stato == 'in_consegna'}">selected</c:if>>In Consegna</option>
                    <option value="consegnato" <c:if test="${param.stato == 'consegnato'}">selected</c:if>>Consegnato</option>
                    <option value="annullato" <c:if test="${param.stato == 'annullato'}">selected</c:if>>Annullato</option>
                </select>
            </div>
            <button type="submit" class="btn">Filtra</button>
            <a href="${pageContext.request.contextPath}/admin/ordini" class="btn btn-secondary">Reset</a>
        </form>

        <div class="table-responsive">
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
                                    <span class="status-badge status-badge-${ordine.stato}">
                                        <c:out value="${ordine.statoLabel}" />
                                    </span>
                                </td>
                                <td>
                                    <c:if test="${ordine.stato == 'in_elaborazione' || ordine.stato == 'in_consegna'}">
                                        <form action="${pageContext.request.contextPath}/admin/ordini" method="post" class="inline-form">
                                            <input type="hidden" name="action" value="aggiornaStato">
                                            <input type="hidden" name="ordineId" value="${ordine.id}">
                                            <label>
                                                <select name="nuovoStato" required>
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
                                            </label>
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
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>
