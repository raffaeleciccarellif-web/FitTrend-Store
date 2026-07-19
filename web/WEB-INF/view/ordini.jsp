<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="rimborsi" type="java.util.Collection<model.Rimborso>"--%>
<%--@elvariable id="ordini" type="java.util.Collection<model.Ordine>"--%>
<%--@elvariable id="errore" type="java.lang.String"--%>
<%--@elvariable id="messaggio" type="java.lang.String"--%>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I Miei Ordini - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="container">
        <h1>I Miei Ordini</h1>

        <c:if test="${not empty errore}">
            <div class="error-msg">
                <c:out value="${errore}" />
            </div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-msg">
                <c:out value="${messaggio}" />
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty ordini}">
                <p>Non hai ancora effettuato nessun ordine.</p>
            </c:when>
            <c:otherwise>
                <div class="ordini-list">
                    <c:forEach var="ordine" items="${ordini}">
                        <div class="ordine-card">
                            <div class="ordine-header">
                                <h3>Ordine #<c:out value="${ordine.id}" /></h3>
                                <p><strong>Data:</strong> <c:out value="${ordine.dataOrdine}" /></p>
                                <p>
                                    <strong>Stato:</strong>
                                    <span class="status-badge ${ordine.stato}">
                                        <c:out value="${ordine.statoLabel}" />
                                    </span>
                                </p>
                                <p><strong>Totale:</strong> &euro; <c:out value="${ordine.totale}" /></p>
                                <p><strong>Indirizzo:</strong> <c:out value="${ordine.indirizzoSpedizione}, ${ordine.capSpedizione} ${ordine.cittaSpedizione}" /></p>
                                <p><strong>Metodo di pagamento:</strong> <c:out value="${ordine.metodoPagamento}" /></p>
                            </div>
                            
                            <div class="ordine-dettagli">
                                <h4>Dettagli Prodotti</h4>
                                <ul>
                                    <c:forEach var="dettaglio" items="${ordine.dettagli}">
                                        <li>
                                            <c:out value="${dettaglio.quantita}x ${dettaglio.nomeProdottoAcquisto}" /> - 
                                            &euro; <c:out value="${dettaglio.prezzoAcquisto}" /> cad.
                                            (Subtotale: &euro; <c:out value="${dettaglio.subtotale}" />)
                                        </li>
                                    </c:forEach>
                                </ul>
                            </div>
                            
                            <div class="ordine-rimborso">
                                <c:set var="rimborso" value="${rimborsi[ordine.id]}" />
                                
                                <c:choose>
                                    <c:when test="${not empty rimborso}">
                                        <div class="rimborso-info">
                                            <h4>Stato Rimborso</h4>
                                            <p><strong>Stato:</strong> <c:out value="${rimborso.statoLabel}" /></p>
                                            <p><strong>Importo:</strong> &euro; <c:out value="${rimborso.importo}" /></p>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${ordine.stato == 'consegnato' || ordine.stato == 'annullato'}">
                                            <form action="${pageContext.request.contextPath}/rimborsi" method="post" class="inline-form">
                                                <input type="hidden" name="action" value="richiedi">
                                                <input type="hidden" name="ordineId" value="${ordine.id}">
                                                <label>
                                                    <input type="text" name="motivo" placeholder="Motivo del rimborso" required class="flex-1">
                                                </label>
                                                <button type="submit" class="btn btn-sm btn-secondary">Richiedi Rimborso</button>
                                            </form>
                                        </c:if>
                                    </c:otherwise>
                                </c:choose>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:otherwise>
        </c:choose>
    </main>

    <jsp:include page="footer.jsp" />
</body>
</html>
