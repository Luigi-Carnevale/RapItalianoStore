<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Risultati ricerca</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

    <jsp:include page="header.jsp" />

    <main class="main-content">
        <div class="container">

            <h2>
                Risultati ricerca per
                "<span style="color:#ff0000">${query}</span>"
            </h2>

            <!-- Caso 1: Album trovati -->
            <c:if test="${not empty albumTrovati}">
                <h3>Album</h3>
                <div class="catalog-grid">
                    <c:forEach var="p" items="${albumTrovati}">
                        <div class="album-card">
                            <div class="album-image">
                                <img src="${pageContext.request.contextPath}/assets/img/${p.immagine}" alt="${p.titolo}">
                            </div>
                            <div class="album-info">
                                <h3 class="album-title">${p.titolo}</h3>
                                <p class="album-price">
                                    € <fmt:formatNumber value="${p.prezzo}" type="number"
                                                        minFractionDigits="2" maxFractionDigits="2"/>
                                </p>
                                <form action="${pageContext.request.contextPath}/carrello" method="post">
                                    <input type="hidden" name="action" value="add">
                                    <input type="hidden" name="idProdotto" value="${p.id}">
                                    <button type="submit" class="add-to-cart">Aggiungi al Carrello</button>
                                </form>
                            </div>
                        </div>

                        <!-- Artista collegato -->
                        <div class="artist-card" style="margin-top:1rem;">
                            <div class="artist-image">
                                <img src="${pageContext.request.contextPath}/assets/img/${p.artista.immagine}"
                                     alt="${p.artista.nome}"
                                     style="width:120px; height:120px; border-radius:50%; object-fit:cover;">
                            </div>
                            <div class="artist-info">
                                <h4>${p.artista.nome}</h4>
                                <p class="artist-genre">${p.artista.genere}</p>
                                <p class="artist-bio">${p.artista.bio}</p>
                            </div>
                        </div>
                    </c:forEach>
                </div>
            </c:if>

            <!-- Caso 2: Artisti trovati -->
            <c:if test="${not empty artistiTrovati}">
                <h3>Artisti</h3>
                <c:forEach var="a" items="${artistiTrovati}">
                    <div class="artist-card" style="margin-bottom:2rem;">
                        <div class="artist-image">
                            <img src="${pageContext.request.contextPath}/assets/img/${a.immagine}"
                                 alt="${a.nome}"
                                 style="width:150px; height:150px; border-radius:50%; object-fit:cover;">
                        </div>
                        <div class="artist-info">
                            <h3 class="artist-name">${a.nome}</h3>
                            <p class="artist-genre">${a.genere}</p>
                            <p class="artist-bio">${a.bio}</p>
                        </div>
                    </div>

                    <!-- Album dell'artista -->
                    <h4>Album di ${a.nome}</h4>
                    <div class="catalog-grid">
                        <c:forEach var="p" items="${a.album}">
                            <div class="album-card">
                                <div class="album-image">
                                    <img src="${pageContext.request.contextPath}/assets/img/${p.immagine}" alt="${p.titolo}">
                                </div>
                                <div class="album-info">
                                    <h3 class="album-title">${p.titolo}</h3>
                                    <p class="album-price">
                                        € <fmt:formatNumber value="${p.prezzo}" type="number"
                                                            minFractionDigits="2" maxFractionDigits="2"/>
                                    </p>
                                </div>
                            </div>
                        </c:forEach>
                    </div>
                </c:forEach>
            </c:if>

            <!-- Nessun risultato -->
            <c:if test="${empty albumTrovati and empty artistiTrovati}">
                <p>Nessun risultato trovato.</p>
            </c:if>

        </div>
    </main>

    <jsp:include page="footer.jsp" />

</body>
</html>
