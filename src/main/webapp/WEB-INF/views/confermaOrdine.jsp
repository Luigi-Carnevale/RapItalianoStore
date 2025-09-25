<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Conferma ordine</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <style>
        /* Banner di conferma ordine */
        .success-banner {
            background-color: #2ecc71; /* verde acceso */
            color: #fff;
            padding: 1rem;
            border-radius: 6px;
            margin-bottom: 1.5rem;
            font-weight: bold;
            text-align: center;
            box-shadow: 0 2px 6px rgba(0,0,0,0.5);
        }
    </style>
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <div class="success-banner">
        ✅ ${msgSuccesso != null ? msgSuccesso : "Ordine completato con successo!"}
    </div>

    <p>Grazie per aver acquistato su <strong>RapItalianoStore</strong>.  
       Puoi controllare lo stato e i dettagli dei tuoi ordini nella sezione <em>I miei ordini</em>.</p>

    <a href="${pageContext.request.contextPath}/ordini" class="btn btn-primary">Vai ai miei ordini</a>
    <a href="${pageContext.request.contextPath}/catalogo" class="btn btn-secondary">Continua lo shopping</a>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
