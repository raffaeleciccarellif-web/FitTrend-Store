<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>

<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
    </div>
</header>

<main class="container">
    <div class="card auth-card">
        <h2>Registrazione</h2>
        
        <c:if test="${not empty errore}">
            <p class="form-error-general">
                <c:out value="${errore}" />
            </p>
        </c:if>

        <form action="${pageContext.request.contextPath}/registrazione" method="post" id="registerForm">
            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" value="<c:out value='${param.nome}' />">
                <span class="error-msg" id="nome-error"></span>
            </div>

            <div class="form-group">
                <label for="cognome">Cognome</label>
                <input type="text" id="cognome" name="cognome" value="<c:out value='${param.cognome}' />">
                <span class="error-msg" id="cognome-error"></span>
            </div>

            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" value="<c:out value='${param.email}' />">
                <span class="error-msg" id="email-error"></span>
            </div>

            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password">
                <span class="error-msg" id="password-error"></span>
            </div>

            <button type="submit" class="btn w-100 mt-md">Registrati</button>
        </form>
        
        <p class="mt-md text-center">
            Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi qui</a>.
        </p>
    </div>
</main>

<footer>
    <div class="container text-center">
        <p>&copy; 2026 FitTrend Store &mdash; Progetto TSW</p>
    </div>
</footer>

<script src="${pageContext.request.contextPath}/scripts/registrazione-validation.js"></script>
</body>
</html>
