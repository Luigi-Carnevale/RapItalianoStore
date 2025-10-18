<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Admin - RapItalianoStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container admin-dashboard">
        <h2>Dashboard Amministratore</h2>
        <div class="admin-buttons">
            <a href="${pageContext.request.contextPath}/utentiAdmin">Gestione Utenti</a>
            <a href="${pageContext.request.contextPath}/prodottiAdmin">Gestione Prodotti</a>
            <a href="${pageContext.request.contextPath}/artistiAdmin">Gestione Artisti</a>
            <a href="${pageContext.request.contextPath}/ordiniAdmin">Gestione Ordini</a>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
