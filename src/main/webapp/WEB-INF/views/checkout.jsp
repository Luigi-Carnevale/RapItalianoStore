<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    <script>
        function aggiornaCampiPagamento() {
            let metodo = document.getElementById("metodoPagamento").value;
            document.getElementById("campiCarta").style.display = metodo === "carta" ? "block" : "none";
            document.getElementById("campiPaypal").style.display = metodo === "paypal" ? "block" : "none";
            document.getElementById("campiBonifico").style.display = metodo === "bonifico" ? "block" : "none";
        }
        document.addEventListener("DOMContentLoaded", aggiornaCampiPagamento);
    </script>
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h1>Checkout</h1>

    <form action="${pageContext.request.contextPath}/checkout" method="post" class="form">

        <!-- Indirizzo di spedizione -->
        <div class="form-group">
            <label for="indirizzo">Indirizzo di spedizione</label>
            <input type="text" id="indirizzo" name="indirizzo" required>
        </div>

        <!-- Metodo di pagamento -->
        <div class="form-group">
            <label for="metodoPagamento">Metodo di pagamento</label>
            <select id="metodoPagamento" name="metodoPagamento" onchange="aggiornaCampiPagamento()">
                <option value="carta">Carta di credito</option>
                <option value="paypal">PayPal</option>
                <option value="bonifico">Bonifico</option>
            </select>
        </div>

        <!-- Campi per Carta di Credito -->
        <div id="campiCarta" class="payment-fields">
            <div class="form-group">
                <label for="numeroCarta">Numero carta</label>
                <input type="text" id="numeroCarta" name="numeroCarta" maxlength="16">
            </div>
            <div class="form-inline">
                <div class="form-group">
                    <label for="scadenza">Scadenza</label>
                    <input type="text" id="scadenza" name="scadenza" placeholder="MM/AA" maxlength="5">
                </div>
                <div class="form-group">
                    <label for="cvv">CVV</label>
                    <input type="password" id="cvv" name="cvv" maxlength="3">
                </div>
            </div>
        </div>

        <!-- Campi per PayPal -->
        <div id="campiPaypal" class="payment-fields">
            <div class="form-group">
                <label for="paypalEmail">Email PayPal</label>
                <input type="email" id="paypalEmail" name="paypalEmail">
            </div>
        </div>

        <!-- Campi per Bonifico -->
        <div id="campiBonifico" class="payment-fields">
            <p>Effettua il bonifico al seguente IBAN:<br>
                <strong>IT60X0542811101000000123456</strong>
            </p>
        </div>

        <button type="submit" class="btn btn-primary">Conferma ordine</button>
    </form>
</main>

<jsp:include page="footer.jsp" />

</body>
</html>
