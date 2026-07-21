<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
        <nav>
            <%-- CATALOGO — sempre visibile --%>
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>

            <%-- CARRELLO — visibile solo a utenti normali loggati --%>
            <c:if test="${not empty sessionScope.utenteLoggato and not sessionScope.utenteLoggato.admin}">
                <a href="${pageContext.request.contextPath}/carrello?action=visualizza">Carrello</a>
            </c:if>

            <c:choose>
                <c:when test="${not empty sessionScope.utenteLoggato}">
                    <c:choose>
                        <c:when test="${sessionScope.utenteLoggato.admin}">
                            <%-- ADMIN — solo se loggato come admin --%>
                            <a href="${pageContext.request.contextPath}/admin/prodotti">Admin Prodotti</a>
                            <a href="${pageContext.request.contextPath}/admin/ordini">Admin Ordini</a>
                            <a href="${pageContext.request.contextPath}/admin/rimborsi">Admin Rimborsi</a>
                        </c:when>
                        <c:otherwise>
                            <%-- I MIEI ORDINI / I MIEI RIMBORSI — solo se loggato come utente normale --%>
                            <a href="${pageContext.request.contextPath}/ordini">I Miei Ordini</a>
                            <a href="${pageContext.request.contextPath}/rimborsi">I Miei Rimborsi</a>
                        </c:otherwise>
                    </c:choose>
                    <%-- ESCI — solo se loggato --%>
                    <a href="${pageContext.request.contextPath}/logout">Esci</a>
                </c:when>
                <c:otherwise>
                    <%-- ACCEDI / REGISTRATI — solo se NON loggato --%>
                    <a href="${pageContext.request.contextPath}/login">Accedi</a>
                    <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>
