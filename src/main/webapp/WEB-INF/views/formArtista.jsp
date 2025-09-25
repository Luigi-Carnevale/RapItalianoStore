<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione artista</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>


<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>
            <c:if test="${empty artista}">Aggiungi Artista</c:if>
            <c:if test="${not empty artista}">Modifica Artista</c:if>
        </h2>

        <form action="${pageContext.request.contextPath}/artistiAdmin"
              method="post"
              enctype="multipart/form-data"
              accept-charset="UTF-8"
              class="admin-form">

            <c:if test="${not empty artista}">
                <input type="hidden" name="id" value="${artista.id}">
                <input type="hidden" name="oldImage" value="${artista.immagine}">
            </c:if>

            <div class="form-group">
                <label for="nome">Nome</label>
                <input type="text" id="nome" name="nome" value="${artista.nome}" required>
            </div>

            <div class="form-group">
                <label for="genere">Genere</label>
                <input type="text" id="genere" name="genere" value="${artista.genere}">
            </div>

            <div class="form-group">
                <label for="bio">Biografia</label>
                <textarea id="bio" name="bio" rows="4">${artista.bio}</textarea>
            </div>

            <div class="form-group">
                <label for="immagine">Immagine</label>
                <input type="file" id="immagine" name="immagine" accept="image/*">
            </div>

            <button type="submit" class="btn btn-primary">Salva</button>
            <a href="${pageContext.request.contextPath}/artistiAdmin" class="btn btn-secondary">Torna alla lista</a>
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">Dashboard Admin</a>
        </form>
    </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
