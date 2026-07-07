<%--
    index.jsp — Entry point di FitTrend Store
    REGOLA: questa è l'UNICA JSP fuori da WEB-INF/view.
    Il suo unico scopo è redirigere alla HomeServlet.
    Non contiene logica applicativa.
--%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<jsp:forward page="/home" />