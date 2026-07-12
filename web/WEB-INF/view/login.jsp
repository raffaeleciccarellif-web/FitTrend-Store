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
    <div class="login-container">
        <h2>Accedi a FitTrend Store</h2>
        
        <c:if test="${not empty errore}">
            <div class="server-error" style="color: red; margin-bottom: 15px;">
                <c:out value="${errore}" />
            </div>
        </c:if>

        <c:if test="${not empty messaggio}">
            <div class="server-message" style="color: green; margin-bottom: 15px;">
                <c:out value="${messaggio}" />
            </div>
        </c:if>
        
        <form id="loginForm" action="${pageContext.request.contextPath}/login" method="POST" novalidate>
            <div class="form-group">
                <label for="email">Email</label>
                <input type="email" id="email" name="email" required>
                <span class="error-msg" id="email-error"></span>
            </div>
            
            <div class="form-group">
                <label for="password">Password</label>
                <input type="password" id="password" name="password" required>
                <span class="error-msg" id="password-error"></span>
            </div>
            
            <button type="submit" class="btn-primary">Accedi</button>
        </form>
        
        <div class="form-footer" style="margin-top: 20px;">
            <p>Non hai un account? <a href="${pageContext.request.contextPath}/registrazione">Registrati qui</a>.</p>
        </div>
    </div>
</body>
</html>
