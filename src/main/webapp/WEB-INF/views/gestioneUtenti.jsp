<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione utenti</title>
	<link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>Gestione Utenti</h2>

        <div class="admin-actions" style="margin-bottom: 1rem;">
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">🏠 Dashboard Admin</a>
        </div>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID</th>
                    <th>Email</th>
                    <th>Ruolo</th>
                    <th>Azioni</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="utente" items="${utenti}">
                    <tr>
                        <td>${utente.id}</td>
                        <td>${utente.email}</td>
                        <td>${utente.ruolo}</td>
                        <td>
                            <a href="${pageContext.request.contextPath}/utentiAdmin?action=delete&id=${utente.id}" 
                               class="btn btn-secondary" 
                               onclick="return confirm('Sei sicuro di voler eliminare questo utente?');">🗑 Elimina</a>

                            <c:if test="${utente.ruolo == 'cliente'}">
                                <a href="${pageContext.request.contextPath}/utentiAdmin?action=promote&id=${utente.id}" 
                                   class="btn btn-secondary">⬆️ Promuovi a Admin</a>
                            </c:if>

                            <c:if test="${utente.ruolo == 'admin'}">
                                <a href="${pageContext.request.contextPath}/utentiAdmin?action=demote&id=${utente.id}" 
                                   class="btn btn-secondary">⬇️ Declassa a Cliente</a>
                            </c:if>
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
