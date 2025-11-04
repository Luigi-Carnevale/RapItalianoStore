<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/fmt"  prefix="fmt" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/functions" prefix="fn" %>

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

<main class="main-content">
  <div class="container">

    <h2 style="margin-bottom:.75rem;">I miei ordini</h2>

    <form action="${pageContext.request.contextPath}/ordini" method="get" class="filter-form">
      <div class="form-inline filters-bar">
        <div class="form-group">
          <label for="dataInizio">Dal</label>
          <input type="date" id="dataInizio" name="dataInizio"
                 value="${fn:escapeXml(dataInizio)}">
        </div>

        <div class="form-group">
          <label for="dataFine">Al</label>
          <input type="date" id="dataFine" name="dataFine"
                 value="${fn:escapeXml(dataFine)}">
        </div>

        <%-- ********* MOD: controllo “Artista” reso coerente con lo stile ********* --%>
        <div class="form-group">
          <label for="idArtista">
            <i class="fas fa-user"></i> Artista
          </label>
          <select id="idArtista" name="idArtista" class="select-control">
            <option value="">Tutti</option>
            <c:forEach var="a" items="${artisti}">
              <option value="${a.id}" <c:if test="${idArtista == a.id}">selected</c:if>>
                ${a.nome}
              </option>
            </c:forEach>
          </select>
        </div>
        <%-- ********* FINE MOD ********* --%>

        <div class="form-group actions" style="display:flex; gap:.5rem;">
          <button type="submit" class="btn btn-primary">
            <i class="fas fa-filter"></i> Filtra
          </button>
          <a class="btn btn-secondary" href="${pageContext.request.contextPath}/ordini">Reset</a>
        </div>
      </div>
    </form>

    <hr>

    <c:if test="${empty ordini}">
      <p>Nessun ordine trovato.</p>
    </c:if>

    <c:forEach var="ordine" items="${ordini}">
      <div class="order-box">
        <h3 style="margin-bottom:.25rem;">
          Ordine #${ordine.id}
        </h3>

        <p style="margin:0 0 .75rem 0; color:#555;">
          <strong>Data:</strong>
          <fmt:formatDate value="${ordine.dataOrdine}" pattern="dd/MM/yyyy HH:mm"/>
          &nbsp;•&nbsp;
          <strong>Totale:</strong>
          € <fmt:formatNumber value="${ordine.totale}" type="number" minFractionDigits="2" maxFractionDigits="2"/>
          &nbsp;•&nbsp;
          <strong>Pagamento:</strong> ${ordine.metodoPagamento}
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
                  <c:choose>
                    <c:when test="${not empty d.titoloProdotto}">
                      <img src="${pageContext.request.contextPath}/assets/img/${d.immagine}"
                           alt="${d.titoloProdotto}" class="cart-thumb">
                      ${d.titoloProdotto}
                    </c:when>
                    <c:otherwise>
                      Prodotto non più a catalogo
                    </c:otherwise>
                  </c:choose>
                </td>
                <td>${d.quantita}</td>
                <td>
                  € <fmt:formatNumber value="${d.prezzoUnitario}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>
                </td>
                <td>
                  € <fmt:formatNumber value="${d.quantita * d.prezzoUnitario}" type="number"
                                      minFractionDigits="2" maxFractionDigits="2"/>
                </td>
              </tr>
            </c:forEach>
          </tbody>
        </table>
      </div>
    </c:forEach>

  </div>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
