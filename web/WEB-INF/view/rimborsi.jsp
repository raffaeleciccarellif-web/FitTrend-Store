<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="rimborsi" type="java.util.Collection<model.Rimborso>"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I Miei Rimborsi - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>I Miei Rimborsi</h1>

        <c:if test="${not empty errore}">
            <div class="error-msg"><c:out value="${errore}" /></div>
        </c:if>

        <c:if test="${not empty messaggio}">
            <div class="success-msg"><c:out value="${messaggio}" /></div>
        </c:if>

        <c:choose>
            <c:when test="${empty rimborsi}">
                <p>Non hai ancora richiesto nessun rimborso.</p>
                <p><a href="${pageContext.request.contextPath}/ordini" class="btn">Vai ai Miei Ordini</a></p>
            </c:when>
            <c:otherwise>
                <div class="table-responsive">
                    <table>
                    <thead>
                        <tr>
                            <th>ID</th>
                            <th>Ordine ID</th>
                            <th>Importo</th>
                            <th>Motivo</th>
                            <th>Data Richiesta</th>
                            <th>Data Elaborazione</th>
                            <th>Stato</th>
                        </tr>
                    </thead>
                    <tbody>
                        <c:forEach var="rimborso" items="${rimborsi}">
                            <tr>
                                <td><c:out value="${rimborso.id}" /></td>
                                <td><c:out value="${rimborso.ordineId}" /></td>
                                <td>&euro; <c:out value="${rimborso.importo}" /></td>
                                <td class="td-motivo" title="<c:out value='${rimborso.motivo}'/>"><c:out value="${rimborso.motivo}" /></td>
                                <td><c:out value="${rimborso.dataRichiesta}" /></td>
                                <td><c:out value="${empty rimborso.dataElaborazione ? '-' : rimborso.dataElaborazione}" /></td>
                                <td>
                                    <span class="status-badge status-badge-${rimborso.stato}">
                                        <c:out value="${rimborso.statoLabel}" />
                                    </span>
                                </td>
                            </tr>
                        </c:forEach>
                    </tbody>
                </table>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>
