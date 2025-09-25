<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>I miei ordini</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h1>I miei ordini</h1>

    <c:if test="${empty ordini}">
        <p>Non hai ancora effettuato ordini.</p>
    </c:if>

    <c:forEach var="ordine" items="${ordini}">
        <div class="order-box">
            <h3>
                Ordine #${ordine.id} – 
                <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/>
            </h3>

            <p>
                <strong>Totale ordine:</strong>
                € <fmt:formatNumber value="${ordine.totale}" type="number" minFractionDigits="2"/><br>
                <strong>Indirizzo di spedizione:</strong> ${ordine.indirizzoSpedizione}<br>
                <strong>Metodo di pagamento:</strong> ${ordine.metodoPagamento}
            </p>

            <table class="order-details">
                <thead>
                    <tr>
                        <th>Prodotto</th>
                        <th>Quantità</th>
                        <th>Prezzo unitario</th>
                        <th>Totale riga</th>
                    </tr>
                </thead>
                <tbody>
                    <c:forEach var="d" items="${ordine.dettagli}">
                        <tr>
                            <td class="cart-product">
                                <img src="${pageContext.request.contextPath}/assets/img/${d.immagine}"
                                     alt="${d.titoloProdotto}" class="cart-thumb">
                                <span>${d.titoloProdotto}</span>
                            </td>
                            <td>${d.quantita}</td>
                            <td>
                                € <fmt:formatNumber value="${d.prezzoUnitario}" type="number" minFractionDigits="2"/>
                            </td>
                            <td>
                                € <fmt:formatNumber value="${d.quantita * d.prezzoUnitario}" type="number" minFractionDigits="2"/>
                            </td>
                        </tr>
                    </c:forEach>
                </tbody>
            </table>
        </div>
    </c:forEach>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
