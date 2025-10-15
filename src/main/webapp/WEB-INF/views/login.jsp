<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Login</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <div class="login-container">
            <h2>Accedi</h2>

            <!-- ✅ Messaggi sessione scaduta o token errato -->
            <c:choose>
                <c:when test="${param.expired == 'inactivity'}">
                    <div class="session-expired-box">
                        <i class="fas fa-hourglass-half"></i>
                        Sei stato inattivo troppo a lungo. La sessione è scaduta, effettua nuovamente il login per riprendere a fare le tue cose.
                    </div>
                </c:when>
                <c:when test="${param.expired == 'true'}">
                    <div class="session-expired-box">
                        <i class="fas fa-clock"></i>
                        La tua sessione è scaduta. Effettua di nuovo il login.
                    </div>
                </c:when>
            </c:choose>

            <!-- Messaggio di successo da registrazione -->
            <c:if test="${not empty msgRegistrazione}">
                <p style="color:green; text-align:center;">${msgRegistrazione}</p>
            </c:if>

            <!-- Messaggio di errore da login fallito -->
            <c:if test="${not empty error}">
                <p style="color:red; text-align:center;">${error}</p>
            </c:if>

            <!-- Form di login -->
            <form class="login-form" action="${pageContext.request.contextPath}/login" method="post">
                <div class="form-group">
                    <label for="email">Email</label>
                    <input type="email" id="email" name="email" required>
                </div>

                <div class="form-group">
                    <label for="password">Password</label>
                    <input type="password" id="password" name="password" required>
                </div>

                <button type="submit" class="btn btn-primary">Login</button>
            </form>

            <div class="login-options">
                <p>Non hai un account? 
                   <a href="${pageContext.request.contextPath}/registrazione">Registrati qui</a>
                </p>
            </div>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

<!-- Script per far svanire il messaggio -->
<script>
document.addEventListener("DOMContentLoaded", function() {
    const box = document.querySelector(".session-expired-box");
    if (box) {
        setTimeout(() => {
            box.style.transition = "opacity 0.8s ease";
            box.style.opacity = "0";
            setTimeout(() => box.remove(), 800);
        }, 5500); // attende 5,5 secondi prima di iniziare a svanire
    }
});
</script>

</body>
</html>
