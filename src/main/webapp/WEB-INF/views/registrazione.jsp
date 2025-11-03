<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Signup</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="login-container">
            <h2>Crea un nuovo account</h2>

            <!-- messaggio di errore lato server -->
            <c:if test="${not empty error}">
                <div class="alert alert-danger" role="alert">${error}</div>
            </c:if>

            <form class="login-form" action="${pageContext.request.contextPath}/registrazione" method="post" autocomplete="off">
                <div class="form-group">
                    <label for="email">Email*</label>
                    <input type="email" id="email" name="email" required maxlength="100">
                </div>

                <!-- username unico -->
                <div class="form-group">
                    <label for="username">Username*</label>
                    <input type="text" id="username" name="username" required maxlength="50"
                           pattern="^[A-Za-z0-9_\\.\\-]{3,50}$"
                           title="3-50 caratteri; lettere, numeri, punto, trattino e underscore.">
                </div>

                <div class="form-group">
                    <label for="password">Password*</label>
                    <input type="password" id="password" name="password" required minlength="6">
                </div>

                <!-- conferma password -->
                <div class="form-group">
                    <label for="conferma">Conferma password*</label>
                    <input type="password" id="conferma" name="conferma" required minlength="6">
                </div>

                <button type="submit" class="btn btn-primary">Registrati</button>
            </form>

            <div class="login-options">
                Hai già un account? <a href="${pageContext.request.contextPath}/login">Accedi qui</a>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
