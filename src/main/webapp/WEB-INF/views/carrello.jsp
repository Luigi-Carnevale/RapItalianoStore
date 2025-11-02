<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt" prefix="fmt" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Carrello</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">

    <%-- Rendo disponibile il context path al JS per le chiamate fetch --%>
    <script>
      window.APP_CTX = '${pageContext.request.contextPath}';
    </script>

    <%-- JS del carrello. Defer per eseguire dopo il parsing del DOM --%>
    <script defer src="${pageContext.request.contextPath}/assets/js/carrello.js"></script>
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h1>Il tuo carrello</h1>

    <c:if test="${empty carrello.items}">
        <p>Il carrello è vuoto.</p>
    </c:if>

    <c:if test="${not empty carrello.items}">
        <table class="cart-table">
            <thead>
                <tr>
                    <th>Prodotto</th>
                    <th>Quantità</th>
                    <th>Prezzo unitario</th>
                    <th>Totale riga</th>
                    <th></th>
                </tr>
            </thead>
            <tbody>
                <c:forEach var="item" items="${carrello.items}">
                    <tr>
                        <td class="cart-product">
                            <img src="${pageContext.request.contextPath}/assets/img/${item.prodotto.immagine}" 
                                 alt="${item.prodotto.titolo}" class="cart-thumb">
                            <span>${item.prodotto.titolo}</span>
                        </td>
                        <td>
                            <%-- Sostituito input number con stepper quantità coerente con il design --%>
                            <div class="qty-control"
                                 data-item-id="${item.prodotto.id}"
                                 data-prezzo="${item.prodotto.prezzo}"
                                 data-min="1"
                                 data-max="99"
                                 aria-label="Controllo quantità">
                                <button type="button" class="qty-btn" aria-label="Diminuisci quantità">−</button>
                                <input
                                  type="number"
                                  class="qty-input"
                                  value="${item.quantita}"
                                  min="1" max="99"
                                  inputmode="numeric" pattern="[0-9]*"
                                  aria-label="Quantità"
                                />
                                <button type="button" class="qty-btn" aria-label="Aumenta quantità">+</button>
                            </div>
                        </td>
                        <td>
                            € <fmt:formatNumber value="${item.prodotto.prezzo}" type="number" minFractionDigits="2"/>
                        </td>
                        <td>
                            <span id="lineTotal-${item.prodotto.id}">
                                € <fmt:formatNumber value="${item.totale}" type="number" minFractionDigits="2"/>
                            </span>
                        </td>
                        <td>
                            <form action="${pageContext.request.contextPath}/carrello" method="post">
                                <input type="hidden" name="action" value="remove">
                                <input type="hidden" name="idProdotto" value="${item.prodotto.id}">
                                <button type="submit" class="btn btn-danger">Rimuovi</button>
                            </form>
                        </td>
                    </tr>
                </c:forEach>
            </tbody>
        </table>

        <div class="cart-summary">
            <h2>
                Totale: <span id="totaleCarrello">
                    € <fmt:formatNumber value="${carrello.totale}" type="number" minFractionDigits="2"/>
                </span>
            </h2>
            <!-- Metodo GET → apre la pagina checkout.jsp -->
            <form action="${pageContext.request.contextPath}/checkout" method="get">
                <button type="submit" class="btn btn-primary">Procedi al checkout</button>
            </form>
        </div>
    </c:if>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
