<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>RapItalianoStore - Catalogo</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>Catalogo Album</h2>

        <!-- 🔎 Barra ricerca + filtri -->
        <form method="get" action="${pageContext.request.contextPath}/catalogo" class="catalog-filters">
            <input type="text" name="q" placeholder="Cerca album..." value="${param.q}" />

            <select name="prezzo">
                <option value="">Tutti i prezzi</option>
                <option value="low" ${param.prezzo == 'low' ? 'selected' : ''}>Meno di €10</option>
                <option value="mid" ${param.prezzo == 'mid' ? 'selected' : ''}>€10 - €20</option>
                <option value="high" ${param.prezzo == 'high' ? 'selected' : ''}>Oltre €20</option>
            </select>

            <select name="artista">
                <option value="">Tutti gli artisti</option>
                <c:forEach var="a" items="${artisti}">
                    <option value="${a.id}" ${param.artista == a.id ? 'selected' : ''}>
                        ${a.nome}
                    </option>
                </c:forEach>
            </select>

            <button type="submit" class="btn btn-primary">Filtra</button>
        </form>

        <!-- Griglia catalogo -->
        <div class="catalog-grid">
            <c:forEach var="prodotto" items="${prodotti}">
                <div class="album-card">
                    
                    <!-- Tutta la card è un link -->
                    <a href="${pageContext.request.contextPath}/prodotto?id=${prodotto.id}" class="album-link">
                        <div class="album-image">
                            <img src="${pageContext.request.contextPath}/assets/img/${prodotto.immagine}"
                                 alt="${prodotto.titolo}">
                        </div>
                        <div class="album-info">
                            <h3 class="album-title">${prodotto.titolo}</h3>
                            <p class="album-artist">${prodotto.artista.nome}</p>
                            <p class="album-price">
                                € <fmt:formatNumber value="${prodotto.prezzo}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                            </p>
                        </div>
                    </a>

                    <!-- Bottone AJAX separato -->
                    <button type="button" class="add-to-cart"
                            onclick="aggiungiAlCarrello(${prodotto.id})">
                        Aggiungi al Carrello
                    </button>
                </div>
            </c:forEach>
        </div>
    </div>
</main>

<jsp:include page="footer.jsp" />

<!-- Script AJAX -->
<script>
function aggiungiAlCarrello(idProdotto) {
    fetch("${pageContext.request.contextPath}/carrello", {
        method: "POST",
        headers: {
            "Content-Type": "application/x-www-form-urlencoded",
            "X-Requested-With": "XMLHttpRequest"
        },
        body: "action=add&idProdotto=" + idProdotto
    })
    .then(res => res.json())
    .then(data => {
        if (data.success) {
            // aggiorna numerino carrello
            document.querySelector(".cart-count").innerText = data.count;

            // messaggio di conferma
            const msg = document.createElement("div");
            msg.className = "msg-success";
            msg.innerText = "Prodotto aggiunto al carrello!";
            document.body.appendChild(msg);

            setTimeout(() => {
                msg.style.opacity = "0";
                setTimeout(() => msg.remove(), 500);
            }, 3000);
        }
    })
    .catch(err => console.error(err));
}
</script>

</body>
</html>
