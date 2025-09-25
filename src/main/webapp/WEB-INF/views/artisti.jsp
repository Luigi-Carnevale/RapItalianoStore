<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Artisti</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <main class="main-content">
        <div class="container">
            <h2>Artisti</h2>

            <!-- 🔎 Ricerca artisti -->
            <form method="get" action="${pageContext.request.contextPath}/artisti" class="catalog-filters">
                <input type="text" name="q" placeholder="Cerca artista..." value="${param.q}" />
                <button type="submit" class="btn btn-primary">Cerca</button>
            </form>

            <div class="artists-grid">
                <c:forEach var="artista" items="${artisti}">
                    <div class="artist-card">
                        <div class="artist-image">
                            <img src="${pageContext.request.contextPath}/assets/img/${artista.immagine}">
                        </div>
                        <div class="artist-info">
                            <h3 class="artist-name">${artista.nome}</h3>
                            <p class="artist-genre">${artista.genere}</p>
                            <p class="artist-bio">${artista.bio}</p>
                        </div>
                    </div>
                </c:forEach>
            </div>
        </div>
    </main>

    <jsp:include page="footer.jsp" />

</body>
</html>
