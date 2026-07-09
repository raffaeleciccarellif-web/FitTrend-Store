<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="it">
<head>
    <meta charset="UTF-8">
    <meta name="viewport" content="width=device-width, initial-scale=1.0">
    <title>FitTrend Store — Home</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/styles/main.css">
</head>
<body>

<header>
    <div class="container">
        <a href="${pageContext.request.contextPath}/home" class="logo">Fit<span>Trend</span></a>
        <nav>
            <a href="${pageContext.request.contextPath}/catalogo">Catalogo</a>
            <a href="${pageContext.request.contextPath}/login">Accedi</a>
            <a href="${pageContext.request.contextPath}/registrazione">Registrati</a>
            <a href="${pageContext.request.contextPath}/carrello?action=visualizza">Carrello</a>
        </nav>
    </div>
</header>

<main>
    <div class="container text-center">
        <h1 class="page-title">Benvenuto su FitTrend Store</h1>
        <p>Il tuo e-commerce dedicato a palestra, fitness e allenamento in casa.</p>
        <p>Accessori, attrezzi, abbigliamento sportivo, shaker, guanti, fasce, tappetini e molto altro.</p>
        <div class="mt-md">
            <a href="${pageContext.request.contextPath}/catalogo" class="btn">Scopri il Catalogo</a>
            <a href="${pageContext.request.contextPath}/registrazione" class="btn btn-secondary">Registrati</a>
        </div>
    </div>
</main>

<footer>
    <div class="container">
        <p>&copy; 2026 FitTrend Store &mdash; Progetto TSW</p>
    </div>
</footer>

</body>
</html>
