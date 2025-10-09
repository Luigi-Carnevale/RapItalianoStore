<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h1>Checkout</h1>

    <!-- Box messaggi -->
    <c:if test="${not empty erroreCheckout}">
        <div class="errore-box">${erroreCheckout}</div>
    </c:if>
    <c:if test="${not empty msgSuccesso}">
        <div class="successo-box">${msgSuccesso}</div>
    </c:if>

    <form action="${pageContext.request.contextPath}/checkout" method="post" class="form" onsubmit="return validaForm(event)">

        <!-- ✅ Token di sessione -->
        <input type="hidden" name="sessionToken" value="${sessionScope.sessionToken}">

        <div id="erroreBox" class="errore-box" style="display:none;"></div>

        <!-- Indirizzo di spedizione -->
        <div class="form-group">
            <label for="indirizzo">Indirizzo di spedizione</label>
            <input type="text" id="indirizzo" name="indirizzo" required>
        </div>

        <!-- Metodo di pagamento -->
        <div class="form-group">
            <label for="metodoPagamento">Metodo di pagamento</label>
            <select id="metodoPagamento" name="metodoPagamento" onchange="aggiornaCampiPagamento()" required>
                <option value="carta">Carta di credito</option>
                <option value="paypal">PayPal</option>
                <option value="bonifico">Bonifico</option>
            </select>
        </div>

        <!-- Campi per Carta -->
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
        <div id="campiPaypal" class="payment-fields" style="display:none;">
            <div class="form-group">
                <label for="paypalEmail">Email PayPal</label>
                <input type="email" id="paypalEmail" name="paypalEmail">
            </div>
        </div>

        <!-- Bonifico -->
        <div id="campiBonifico" class="payment-fields" style="display:none;">
            <p>Effettua il bonifico al seguente IBAN:<br><strong>IT60X0542811101000000123456</strong></p>
        </div>

        <button type="submit" class="btn btn-primary">Conferma ordine</button>
    </form>
</main>

<jsp:include page="footer.jsp" />

<script>
function aggiornaCampiPagamento() {
    let metodo = document.getElementById("metodoPagamento").value;
    document.getElementById("campiCarta").style.display = metodo === "carta" ? "block" : "none";
    document.getElementById("campiPaypal").style.display = metodo === "paypal" ? "block" : "none";
    document.getElementById("campiBonifico").style.display = metodo === "bonifico" ? "block" : "none";
}

function mostraErrore(msg) {
    const box = document.getElementById("erroreBox");
    box.innerText = msg;
    box.style.display = "block";
}

function validaForm(e) {
    const metodo = document.getElementById("metodoPagamento").value;
    const num = document.getElementById("numeroCarta").value.trim();
    const scad = document.getElementById("scadenza").value.trim();
    const cvv = document.getElementById("cvv").value.trim();
    const email = document.getElementById("paypalEmail").value.trim();

    document.getElementById("erroreBox").style.display = "none";

    if (metodo === "carta") {
        if (num.length !== 16 || isNaN(num)) { mostraErrore("Numero carta non valido"); e.preventDefault(); return false; }
        if (!/^(0[1-9]|1[0-2])\/\d{2}$/.test(scad)) { mostraErrore("Data scadenza non valida"); e.preventDefault(); return false; }
        if (cvv.length !== 3 || isNaN(cvv)) { mostraErrore("CVV non valido"); e.preventDefault(); return false; }
    } else if (metodo === "paypal" && email === "") {
        mostraErrore("Inserisci l'email PayPal.");
        e.preventDefault();
        return false;
    }
    return true;
}
document.addEventListener("DOMContentLoaded", aggiornaCampiPagamento);
</script>
</body>
</html>
