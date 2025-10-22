<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Gestione ordini</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>

<body>

<jsp:include page="header.jsp" />

<main class="main-content">
    <div class="container">
        <h2>Gestione Ordini</h2>

        <div class="admin-actions" style="margin-bottom: 1rem;">
            <a href="${pageContext.request.contextPath}/admin" class="btn btn-secondary">🏠 Dashboard Admin</a>
        </div>

        <table class="admin-table">
            <thead>
                <tr>
                    <th>ID Ordine</th>
                    <th>Utente</th>
                    <th>Data</th>
                    <th>Totale</th>
                    <th>Dettagli</th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="ordine" items="${ordini}">
                    <tr>
                        <td>${ordine.id}</td>
                        <td>${ordine.utente.email}</td>
                        <td>
                            <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/>
                        </td>
                        <td>
                            € <fmt:formatNumber value="${ordine.totale}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
                        </td>
                        <td>
                            <a href="${pageContext.request.contextPath}/ordiniAdmin?action=view&id=${ordine.id}" 
                               class="btn btn-secondary">📄 Vedi Dettagli</a>
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
