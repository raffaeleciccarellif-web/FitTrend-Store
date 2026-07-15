<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
        <nav>
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
            <a href="${pageContext.request.contextPath}/carrello?action=visualizza">Carrello</a>
            <c:choose>
                <c:when test="${not empty sessionScope.utenteLoggato}">
                    <c:choose>
                        <c:when test="${sessionScope.utenteLoggato.admin}">
                            <a href="${pageContext.request.contextPath}/admin/prodotti">Admin Prodotti</a>
                            <a href="${pageContext.request.contextPath}/admin/ordini">Admin Ordini</a>
                            <a href="${pageContext.request.contextPath}/admin/rimborsi">Admin Rimborsi</a>
                        </c:when>
                        <c:otherwise>
                            <a href="${pageContext.request.contextPath}/ordini">I Miei Ordini</a>
                            <a href="${pageContext.request.contextPath}/rimborsi">I Miei Rimborsi</a>
                        </c:otherwise>
                    </c:choose>
                    <a href="${pageContext.request.contextPath}/logout">Esci</a>
                </c:when>
                <c:otherwise>
                    <a href="${pageContext.request.contextPath}/login">Accedi</a>
                    <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
                </c:otherwise>
            </c:choose>
        </nav>
    </div>
</header>
