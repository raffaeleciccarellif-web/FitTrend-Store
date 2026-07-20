<%@ page contentType="text/html;charset=UTF-8" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>

<%--@elvariable id="errore" type="java.lang.String"--%>

<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>Registrazione - FitTrend Store</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <a href="${pageContext.request.contextPath}/home" class="back-link">&larr; Home</a>
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
                <label for="password" class="label-required">Password</label>
                <div class="input-password-wrap">
                    <input type="password" id="password" name="password" required minlength="8">
                    <button type="button" class="toggle-password" onclick="togglePassword('password', this)" title="Mostra password" aria-label="Mostra password"><svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg></button>
                </div>
                <span class="field-hint">Minimo 8 caratteri</span>
                <span class="error-msg" id="password-error"></span>
            </div>

            <button type="submit" class="btn w-100 mt-md">Registrati</button>
        </form>
        
        <p class="mt-md text-center">
            Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi qui</a>.
        </p>
    </div>
</main>

<jsp:include page="footer.jsp" />

<script src="${pageContext.request.contextPath}../scripts/registrazione-validation.js"></script>
<script>
function togglePassword(inputId, btn) {
    var input = document.getElementById(inputId);
    var isHidden = input.type === 'password';
    input.type = isHidden ? 'text' : 'password';
    btn.innerHTML = isHidden
        ? '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M17.94 17.94A10.07 10.07 0 0 1 12 20c-7 0-11-8-11-8a18.45 18.45 0 0 1 5.06-5.94"/><path d="M9.9 4.24A9.12 9.12 0 0 1 12 4c7 0 11 8 11 8a18.5 18.5 0 0 1-2.16 3.19"/><line x1="1" y1="1" x2="23" y2="23"/></svg>'
        : '<svg xmlns="http://www.w3.org/2000/svg" width="16" height="16" viewBox="0 0 24 24" fill="none" stroke="currentColor" stroke-width="2" stroke-linecap="round" stroke-linejoin="round"><path d="M1 12s4-8 11-8 11 8 11 8-4 8-11 8-11-8-11-8z"/><circle cx="12" cy="12" r="3"/></svg>';
    btn.title = isHidden ? 'Nascondi password' : 'Mostra password';
}
</script>
</body>
</html>
