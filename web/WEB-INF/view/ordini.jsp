<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I Miei Ordini - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/style.css">
    <style>
        .ordine-card { border: 1px solid #ddd; margin-bottom: 20px; padding: 15px; border-radius: 5px; }
        .ordine-header { border-bottom: 1px solid #eee; padding-bottom: 10px; margin-bottom: 10px; }
        .ordine-rimborso { margin-top: 15px; padding-top: 10px; border-top: 1px solid #eee; }
        .badge { display: inline-block; padding: 3px 8px; border-radius: 12px; font-size: 0.8em; font-weight: bold; background: #eee; }
        .badge-consegnato { background: #d4edda; color: #155724; }
        .badge-in_elaborazione { background: #fff3cd; color: #856404; }
        .badge-in_consegna { background: #cce5ff; color: #004085; }
        .badge-annullato { background: #f8d7da; color: #721c24; }
    </style>
</head>
<body>
    <jsp:include page="header.jsp" />

    <main class="ordini-container" style="max-width: 800px; margin: 0 auto; padding: 20px;">
        <h1>I Miei Ordini</h1>

        <c:if test="${not empty errore}">
            <div class="error-message" style="color: red; margin-bottom: 15px;">
                <c:out value="${errore}" />
            </div>
        </c:if>
        
        <c:if test="${not empty messaggio}">
            <div class="success-message" style="color: green; margin-bottom: 15px;">
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
                                    <span class="badge badge-${ordine.stato}">
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
                                            <form action="${pageContext.request.contextPath}/rimborsi" method="post" style="display:flex; gap: 10px; align-items: center;">
                                                <input type="hidden" name="action" value="richiedi">
                                                <input type="hidden" name="ordineId" value="${ordine.id}">
                                                <input type="text" name="motivo" placeholder="Motivo del rimborso" required style="padding: 5px; flex: 1;">
                                                <button type="submit" class="btn btn-secondary" style="padding: 5px 10px;">Richiedi Rimborso</button>
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
