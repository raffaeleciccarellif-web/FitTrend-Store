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
    <div class="card auth-card">
        <h2>Accedi a FitTrend Store</h2>
        
        <c:if test="${not empty errore}">
            <p class="form-error-general">
                <c:out value="${errore}" />
            </p>
        </c:if>

        <c:if test="${not empty messaggio}">
            <p class="success-msg">
                <c:out value="${messaggio}" />
            </p>
        </c:if>
        
        <form id="loginForm" action="${pageContext.request.contextPath}/login" method="POST" novalidate>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required value="<c:out value='${param.email}' />">
                <span class="error-msg" id="email-error"></span>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
                <span class="error-msg" id="password-error"></span>
            </div>
            
            <button type="submit" class="btn w-100 mt-md">Accedi</button>
        </form>
        
        <p class="mt-md text-center">
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
