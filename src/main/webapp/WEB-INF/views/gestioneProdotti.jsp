<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione prodotti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>Gestione Prodotti</h2>

        <div class="admin-actions" style="margin-bottom: 1rem;">
            <a href="${pageContext.request.contextPath}/prodottiAdmin?action=addForm" class="btn btn-primary">➕ Aggiungi Prodotto</a>
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">🏠 Dashboard Admin</a>
        </div>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Titolo</th>
                    <th>Prezzo</th>
                    <th>Artista</th>
                    <th>Immagine</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="prodotto" items="${prodotti}">
                    <tr>
                        <td>${prodotto.id}</td>
                        <td>${prodotto.titolo}</td>
                        <td>€ ${prodotto.prezzo}</td>
                        <td>${prodotto.artista.nome}</td>
                        <td><img src="${pageContext.request.contextPath}/assets/img/${prodotto.immagine}" alt="${prodotto.titolo}" style="width:50px; height:50px; object-fit:cover;"></td>
                        <td>
                            <a href="${pageContext.request.contextPath}/prodottiAdmin?action=edit&id=${prodotto.id}" class="btn btn-secondary">✏️ Modifica</a>
                            <a href="${pageContext.request.contextPath}/prodottiAdmin?action=delete&id=${prodotto.id}" class="btn btn-secondary" onclick="return confirm('Sei sicuro di voler eliminare questo prodotto?');">🗑 Elimina</a>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>
    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
