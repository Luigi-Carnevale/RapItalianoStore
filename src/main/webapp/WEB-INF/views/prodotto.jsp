<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>
<%@ page contentType="text/html; charset=UTF-8" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>${prodotto.titolo} - RapItalianoStore</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>
<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">

        <!-- Sezione principale -->
        <div class="product-detail">
            <!-- Immagine -->
            <div class="product-image">
                <img src="${pageContext.request.contextPath}/assets/img/${prodotto.immagine}" 
                     alt="${prodotto.titolo}">
            </div>

            <!-- Info prodotto -->
            <div class="product-info">
                <h2>${prodotto.titolo}</h2>
                <p class="album-artist">di ${prodotto.artista.nome}</p>
                <p class="album-price">
                    € <fmt:formatNumber value="${prodotto.prezzo}" type="number" 
                                        minFractionDigits="2" maxFractionDigits="2"/>
                </p>
                <p class="album-description">
                    <c:choose>
                        <c:when test="${not empty prodotto.descrizione}">
                            ${prodotto.descrizione}
                        </c:when>
                        <c:otherwise>
                            Nessuna descrizione disponibile.
                        </c:otherwise>
                    </c:choose>
                </p>

                <!-- Aggiungi al carrello -->
                <form action="${pageContext.request.contextPath}/carrello" method="post">
                    <input type="hidden" name="action" value="add">
                    <input type="hidden" name="idProdotto" value="${prodotto.id}">
                    <button type="submit" class="btn btn-primary">Aggiungi al Carrello</button>
                </form>
            </div>
        </div>

        <!-- Sezione recensioni -->
        <div class="reviews-section">
            <h3>Recensioni</h3>

            <c:if test="${empty recensioni}">
                <p>Non ci sono ancora recensioni per questo prodotto.</p>
            </c:if>

            <c:forEach var="r" items="${recensioni}">
                <div class="review-card">
                    <div class="review-header">
                        <strong>${r.utente.email}</strong>
                        <span class="review-stars">
                            <c:forEach begin="1" end="5" var="i">
                                <i class="${i <= r.valutazione ? 'fas' : 'far'} fa-star"></i>
                            </c:forEach>
                        </span>
                        <span class="review-date">
                            (<fmt:formatDate value="${r.dataRecensione}" pattern="dd/MM/yyyy"/>)
                        </span>
                    </div>
                    <p class="review-comment">${r.commento}</p>
                </div>
            </c:forEach>

            <!-- Solo utenti loggati possono recensire -->
            <c:if test="${sessionScope.utente != null}">
                <div class="review-form">
                    <h4>Lascia una recensione</h4>
                    <form action="${pageContext.request.contextPath}/recensioni" method="post">
                        <input type="hidden" name="idProdotto" value="${prodotto.id}">

                        <label for="valutazione">Valutazione:</label>
                        <select name="valutazione" id="valutazione" required>
                            <option value="5">⭐⭐⭐⭐⭐</option>
                            <option value="4">⭐⭐⭐⭐</option>
                            <option value="3">⭐⭐⭐</option>
                            <option value="2">⭐⭐</option>
                            <option value="1">⭐</option>
                        </select>

                        <label for="commento">Commento:</label>
                        <textarea name="commento" id="commento" rows="3" required></textarea>

                        <button type="submit" class="btn btn-secondary">Invia recensione</button>
                    </form>
                </div>
            </c:if>
        </div>

    </div>
</main>

<jsp:include page="footer.jsp" />
</body>
</html>
