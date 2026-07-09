<%-- index.jsp: punto di ingresso minimale, redirige alla HomeServlet --%>
<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<% response.sendRedirect(request.getContextPath() + "/home"); %>