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

        <%-- Page header --%>
        <div class="ordini-page-header">
            <div class="ordini-page-icon"></div>
            <div>
                <h1 class="ordini-page-title">I Miei Ordini</h1>
                <p class="ordini-page-subtitle">Consulta lo storico dei tuoi acquisti e gestisci i rimborsi</p>
            </div>
        </div>

        <%-- Error message --%>
        <c:if test="${not empty errore}">
            <div class="error-msg">
                <c:out value="${errore}" />
            </div>
        </c:if>

        <%-- Success message --%>
        <c:if test="${not empty messaggio}">
            <div class="success-msg">
                <c:out value="${messaggio}" />
            </div>
        </c:if>

        <c:choose>
            <c:when test="${empty ordini}">
                <div class="ordini-empty">

                    <h2>Nessun ordine trovato</h2>
                    <p>Non hai ancora effettuato nessun ordine. Inizia a fare shopping!</p>
                    <a href="${pageContext.request.contextPath}/catalogo" class="btn">Vai al Catalogo</a>
                </div>
            </c:when>
            <c:otherwise>
                <div class="ordini-list">
                    <c:forEach var="ordine" items="${ordini}">
                        <div class="ordine-card">

                            <%-- Card top bar with order number and status --%>
                            <div class="ordine-topbar">
                                <div class="ordine-topbar-left">
                                    <span class="ordine-numero">Ordine #<c:out value="${ordine.id}" /></span>
                                    <span class="ordine-data">
                                        <c:out value="${ordine.dataOrdine}" />
                                    </span>
                                </div>
                                <span class="status-badge ${ordine.stato}">
                                    <c:out value="${ordine.statoLabel}" />
                                </span>
                            </div>

                            <%-- Order info grid --%>
                            <div class="ordine-info-grid">
                                <div class="ordine-info-item">
                                    <span class="ordine-info-label">Totale</span>
                                    <span class="ordine-info-value ordine-totale">&euro; <c:out value="${ordine.totale}" /></span>
                                </div>
                                <div class="ordine-info-item">
                                    <span class="ordine-info-label">Indirizzo di spedizione</span>
                                    <span class="ordine-info-value">
                                        <c:out value="${ordine.indirizzoSpedizione}" />,
                                        <c:out value="${ordine.capSpedizione}" />
                                        <c:out value="${ordine.cittaSpedizione}" />
                                    </span>
                                </div>
                                <div class="ordine-info-item">
                                    <span class="ordine-info-label">Metodo di pagamento</span>
                                    <span class="ordine-info-value"><c:out value="${ordine.metodoPagamento}" /></span>
                                </div>
                            </div>

                            <%-- Product details table --%>
                            <div class="ordine-dettagli">
                                <h4 class="ordine-dettagli-title">Prodotti ordinati</h4>
                                <div class="table-responsive">
                                    <table>
                                        <thead>
                                            <tr>
                                                <th>Prodotto</th>
                                                <th>Quantit&agrave;</th>
                                                <th>Prezzo unitario</th>
                                                <th>Subtotale</th>
                                            </tr>
                                        </thead>
                                        <tbody>
                                            <c:forEach var="dettaglio" items="${ordine.dettagli}">
                                                <tr>
                                                    <td><c:out value="${dettaglio.nomeProdottoAcquisto}" /></td>
                                                    <td><c:out value="${dettaglio.quantita}" /></td>
                                                    <td>&euro; <c:out value="${dettaglio.prezzoAcquisto}" /></td>
                                                    <td><strong>&euro; <c:out value="${dettaglio.subtotale}" /></strong></td>
                                                </tr>
                                            </c:forEach>
                                        </tbody>
                                    </table>
                                </div>
                            </div>

                            <%-- Refund section --%>
                            <div class="ordine-rimborso">
                                <c:set var="rimborso" value="${rimborsi[ordine.id]}" />

                                <c:choose>
                                    <c:when test="${not empty rimborso}">
                                        <div class="rimborso-info">
                                            <h4 class="rimborso-info-title">Stato Rimborso</h4>
                                            <div class="rimborso-info-grid">
                                                <div>
                                                    <span class="ordine-info-label">Stato</span>
                                                    <span class="status-badge ${rimborso.stato}">
                                                        <c:out value="${rimborso.statoLabel}" />
                                                    </span>
                                                </div>
                                                <div>
                                                    <span class="ordine-info-label">Importo</span>
                                                    <span class="ordine-info-value ordine-totale">&euro; <c:out value="${rimborso.importo}" /></span>
                                                </div>
                                            </div>
                                        </div>
                                    </c:when>
                                    <c:otherwise>
                                        <c:if test="${ordine.stato == 'consegnato' || ordine.stato == 'annullato'}">
                                            <form action="${pageContext.request.contextPath}/rimborsi" method="post" class="rimborso-form">
                                                <input type="hidden" name="action" value="richiedi">
                                                <input type="hidden" name="ordineId" value="${ordine.id}">
                                                <label>
                                                    <input type="text" name="motivo" placeholder="Descrivi il motivo del rimborso..." required>
                                                </label>
                                                <button type="submit" class="btn btn-secondary btn-sm">Richiedi Rimborso</button>
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
