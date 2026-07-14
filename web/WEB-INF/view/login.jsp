<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Login - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
    <script src="${pageContext.request.contextPath}/scripts/login-validation.js" defer></script>
</head>
<body>

<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
    </div>
</header>

<main class="container">
    <div class="card" style="max-width: 400px; margin: 40px auto; padding: 20px;">
        <h2>Accedi a FitTrend Store</h2>
        
        <c:if test="${not empty errore}">
            <p class="form-error-general" style="color: red; margin-bottom: 15px;">
                <c:out value="${errore}" />
            </p>
        </c:if>

        <c:if test="${not empty messaggio}">
            <p class="server-message" style="color: green; margin-bottom: 15px;">
                <c:out value="${messaggio}" />
            </p>
        </c:if>
        
        <form id="loginForm" action="${pageContext.request.contextPath}/login" method="POST" novalidate>
            <div class="form-group" style="margin-bottom: 15px;">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" style="width: 100%;" required value="<c:out value='${param.email}' />">
                <span class="error-msg" id="email-error" style="color: red; font-size: 0.9em;"></span>
            </div>
            
            <div class="form-group" style="margin-bottom: 15px;">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" style="width: 100%;" required>
                <span class="error-msg" id="password-error" style="color: red; font-size: 0.9em;"></span>
            </div>
            
            <button type="submit" class="btn" style="width: 100%; margin-top: 15px;">Accedi</button>
        </form>
        
        <p style="margin-top: 20px; text-align: center;">
            Non hai un account? <a href="${pageContext.request.contextPath}/registrazione">Registrati qui</a>.
        </p>
    </div>
</main>

<footer>
    <div class="container text-center">
        <p>&copy; 2026 FitTrend Store &mdash; Progetto TSW</p>
    </div>
</footer>

</body>
</html>
