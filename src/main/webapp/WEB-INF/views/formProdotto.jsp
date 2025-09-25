<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione prodotto</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>
            <c:if test="${empty prodotto}">Aggiungi Prodotto</c:if>
            <c:if test="${not empty prodotto}">Modifica Prodotto</c:if>
        </h2>

        <form action="${pageContext.request.contextPath}/prodottiAdmin"
              method="post"
              enctype="multipart/form-data"
              accept-charset="UTF-8"
              class="admin-form">

            <c:if test="${not empty prodotto}">
                <input type="hidden" name="id" value="${prodotto.id}">
                <input type="hidden" name="oldImage" value="${prodotto.immagine}">
            </c:if>

            <!-- Titolo -->
            <div class="form-group">
                <label for="titolo">Titolo</label>
                <input type="text" id="titolo" name="titolo" value="${prodotto.titolo}" required>
            </div>

            <!-- Descrizione -->
            <div class="form-group">
                <label for="descrizione">Descrizione</label>
                <textarea id="descrizione" name="descrizione" rows="5" required>
${prodotto.descrizione}
                </textarea>
            </div>

            <!-- Prezzo -->
            <div class="form-group">
                <label for="prezzo">Prezzo</label>
                <input type="number" step="0.01" id="prezzo" name="prezzo" value="${prodotto.prezzo}" required>
            </div>

            <!-- Immagine -->
            <div class="form-group">
                <label for="immagine">Immagine</label>
                <input type="file" id="immagine" name="immagine" accept="image/*">
            </div>

            <!-- Artista -->
            <div class="form-group">
                <label for="idArtista">Artista</label>
                <select id="idArtista" name="idArtista" required>
                    <c:forEach var="artista" items="${artisti}">
                        <option value="${artista.id}" <c:if test="${prodotto.idArtista == artista.id}">selected</c:if>>
                            ${artista.nome}
                        </option>
                    </c:forEach>
                </select>
            </div>

            <!-- Pulsanti -->
            <button type="submit" class="btn btn-primary">Salva</button>
            <a href="${pageContext.request.contextPath}/prodottiAdmin" class="btn btn-secondary">Torna alla lista</a>
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">Dashboard Admin</a>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
