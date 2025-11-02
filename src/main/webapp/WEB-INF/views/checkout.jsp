<%@ page contentType="text/html; charset=UTF-8" %>
<%@ taglib uri="http://java.sun.com/jsp/jstl/core" prefix="c" %>

<html lang="it">
<head>
    <meta charset="UTF-8">
    <title>Checkout</title>
    <link rel="stylesheet" href="${pageContext.request.contextPath}/assets/css/styles.css">
    <link href="https://fonts.googleapis.com/css2?family=Roboto:wght@300;400;700;900&display=swap" rel="stylesheet">
    <link rel="stylesheet" href="https://cdnjs.cloudflare.com/ajax/libs/font-awesome/6.0.0/css/all.min.css">
    
    <script defer src="${pageContext.request.contextPath}/assets/js/checkout.js"></script>
    
</head>
<body>

<jsp:include page="header.jsp" />

<main class="container">
    <h1>Checkout</h1>

    <!-- Box messaggi lato server (fallback) -->
    <c:if test="${not empty erroreCheckout}">
        <div class="errore-box">${erroreCheckout}</div>
    </c:if>
    <c:if test="${not empty msgSuccesso}">
        <div class="successo-box">${msgSuccesso}</div>
    </c:if>

    <!--
      MOD: onsubmit intercettato via JS:
           1) facciamo validazione AJAX completa (senza ricaricare)
           2) se ok, proseguiamo con submit normale (server salva ordine)
    -->
    <form id="checkoutForm" action="${pageContext.request.contextPath}/checkout" method="post" class="form">

        <!-- ✅ Token di sessione (CSRF/sessione) -->
        <input type="hidden" name="sessionToken" value="${sessionScope.sessionToken}">

        <!-- =======================
             Dati di spedizione
             ======================= -->
        <div class="form-grid-2">
            <div class="form-group">
                <label for="nome">Nome *</label>
                <input type="text" id="nome" name="nome" required>
                <small class="hint" id="hint-nome"></small>
                <small class="error-msg" id="err-nome"></small> <!-- MOD: errore inline -->
            </div>
            <div class="form-group">
                <label for="cognome">Cognome *</label>
                <input type="text" id="cognome" name="cognome" required>
                <small class="hint" id="hint-cognome"></small>
                <small class="error-msg" id="err-cognome"></small>
            </div>
        </div>

        <div class="form-group">
            <label for="indirizzo">Indirizzo *</label>
            <input type="text" id="indirizzo" name="indirizzo" placeholder="Via e numero civico" required>
            <small class="hint" id="hint-indirizzo"></small>
            <small class="error-msg" id="err-indirizzo"></small>
        </div>

        <div class="form-grid-3">
            <div class="form-group">
                <label for="cap">CAP *</label>
                <input type="text" id="cap" name="cap" maxlength="10" required>
                <small class="hint" id="hint-cap"></small>
                <small class="error-msg" id="err-cap"></small>
            </div>
            <div class="form-group">
                <label for="citta">Città *</label>
                <input type="text" id="citta" name="citta" required>
                <small class="hint" id="hint-citta"></small>
                <small class="error-msg" id="err-citta"></small>
            </div>
            <div class="form-group">
                <label for="provincia">Provincia *</label>
                <input type="text" id="provincia" name="provincia" maxlength="2" placeholder="es. NA" required>
                <small class="hint" id="hint-provincia"></small>
                <small class="error-msg" id="err-provincia"></small>
            </div>
        </div>

        <div class="form-grid-2">
            <div class="form-group">
                <label for="paese">Paese *</label>
                <input type="text" id="paese" name="paese" value="Italia" required>
                <small class="hint" id="hint-paese"></small>
                <small class="error-msg" id="err-paese"></small>
            </div>
            <div class="form-group">
                <label for="telefono">Telefono *</label>
                <input type="tel" id="telefono" name="telefono" placeholder="+39 3xxxxxxxxx" required>
                <small class="hint" id="hint-telefono"></small>
                <small class="error-msg" id="err-telefono"></small>
            </div>
        </div>

        <!-- =======================
             Metodo di pagamento
             ======================= -->
        <div class="form-group">
            <label for="metodoPagamento">Metodo di pagamento *</label>
            <select id="metodoPagamento" name="metodoPagamento" onchange="aggiornaCampiPagamento()" required>
                <option value="carta">Carta di credito</option>
                <option value="paypal">PayPal</option>
                <option value="bonifico">Bonifico</option>
            </select>
            <small class="error-msg" id="err-metodoPagamento"></small>
        </div>

        <!-- Campi per Carta -->
        <div id="campiCarta" class="payment-fields">
            <div class="form-group">
                <label for="numeroCarta">Numero carta *</label>
                <input type="text" id="numeroCarta" name="numeroCarta" maxlength="16">
                <small class="hint" id="hint-numeroCarta"></small>
                <small class="error-msg" id="err-numeroCarta"></small>
            </div>
            <div class="form-inline">
                <div class="form-group">
                    <label for="scadenza">Scadenza *</label>
                    <input type="text" id="scadenza" name="scadenza" placeholder="MM/AA" maxlength="5">
                    <small class="hint" id="hint-scadenza"></small>
                    <small class="error-msg" id="err-scadenza"></small>
                </div>
                <div class="form-group">
                    <label for="cvv">CVV *</label>
                    <input type="password" id="cvv" name="cvv" maxlength="3">
                    <small class="hint" id="hint-cvv"></small>
                    <small class="error-msg" id="err-cvv"></small>
                </div>
            </div>
        </div>

        <!-- Campi per PayPal -->
        <div id="campiPaypal" class="payment-fields" style="display:none;">
            <div class="form-group">
                <label for="paypalEmail">Email PayPal *</label>
                <input type="email" id="paypalEmail" name="paypalEmail">
                <small class="hint" id="hint-paypalEmail"></small>
                <small class="error-msg" id="err-paypalEmail"></small>
            </div>
        </div>

        <!-- Bonifico -->
        <div id="campiBonifico" class="payment-fields" style="display:none;">
            <p>Effettua il bonifico al seguente IBAN:<br><strong>IT60X0542811101000000123456</strong></p>
        </div>

        <button id="btnConferma" type="submit" class="btn btn-primary">Conferma ordine</button>
    </form>
</main>

<jsp:include page="footer.jsp" />

<script>
/* ===========================================================
   Helpers UI: toggle campi pagamento e gestione errori inline
   =========================================================== */
function aggiornaCampiPagamento() {
    let metodo = document.getElementById("metodoPagamento").value;
    document.getElementById("campiCarta").style.display   = (metodo === "carta")   ? "block" : "none";
    document.getElementById("campiPaypal").style.display  = (metodo === "paypal")  ? "block" : "none";
    document.getElementById("campiBonifico").style.display= (metodo === "bonifico")? "block" : "none";
}

/* Pulisce tutti i messaggi di errore inline */
function clearAllErrors(){
    document.querySelectorAll(".error-msg").forEach(el => { el.textContent = ""; });
    document.querySelectorAll(".form-group input, .form-group select, .form-group textarea")
        .forEach(el => el.classList.remove("input-error"));
}

/* Mostra un errore sotto il campo e aggiunge classe evidenza */
function showFieldError(fieldId, message){
    const err = document.getElementById("err-" + fieldId);
    const input = document.getElementById(fieldId);
    if (err) err.textContent = message || "Valore non valido";
    if (input) input.classList.add("input-error");
}

/* ============================
   AJAX: validazione singolo campo (già presente)
   ============================ */
async function validaCampoAjax(field, value) {
    try {
        const resp = await fetch("${pageContext.request.contextPath}/checkout", {
            method: "POST",
            headers: { "Content-Type": "application/x-www-form-urlencoded" },
            body: "ajax=1&field=" + encodeURIComponent(field) + "&value=" + encodeURIComponent(value)
        });
        const data = await resp.json(); // {ok, message?}
        return data;
    } catch (e) {
        // non bloccare in caso di errore rete
        return { ok: true };
    }
}

// debounce semplice
function debounce(fn, delay=300){
    let t; 
    return (...args) => { clearTimeout(t); t = setTimeout(()=>fn(...args), delay); }
}

document.addEventListener("DOMContentLoaded", () => {
    aggiornaCampiPagamento();

    const campi = [
        "nome","cognome","indirizzo","cap","citta","provincia","paese","telefono",
        "numeroCarta","scadenza","cvv","paypalEmail"
    ];

    // Validazione live campo-per-campo (hint ✓/errore breve)
    campi.forEach(id => {
        const el = document.getElementById(id);
        if (!el) return;
        const hint = document.getElementById("hint-" + id);
        const handler = debounce(async () => {
            const v = el.value.trim();
            // ripulisci solo il messaggio di quel campo alla digitazione
            const errEl = document.getElementById("err-" + id);
            if (errEl) errEl.textContent = "";
            el.classList.remove("input-error");

            if (!v) { if (hint){ hint.textContent=""; } return; }
            const res = await validaCampoAjax(id, v);
            if (hint) {
                hint.textContent = res.ok ? "✓" : (res.message || "Valore non valido");
                hint.style.color = res.ok ? "green" : "red";
            }
        }, 300);
        el.addEventListener("input", handler);
        el.addEventListener("blur", handler);
    });

    // =============================
    // PRE-CHECK AJAX al submit
    // =============================
    const form = document.getElementById("checkoutForm");
    form.addEventListener("submit", async (e) => {
        clearAllErrors(); // pulisci errori vecchi
        e.preventDefault(); // blocco invio finché non validiamo via AJAX

        const fd = new URLSearchParams(new FormData(form));
        fd.append("ajax", "1");
        fd.append("mode", "all"); // MOD: validazione completa lato server

        try {
            const resp = await fetch("${pageContext.request.contextPath}/checkout", {
                method: "POST",
                headers: { "Content-Type": "application/x-www-form-urlencoded" },
                body: fd.toString()
            });
            const data = await resp.json(); // {ok:boolean, errors?:{field:msg}, focus?:field}

            if (data.ok) {
                // Tutto valido -> procedi con submit normale (salva ordine)
                form.submit();
                return;
            }

            // Visualizza errori inline per i campi
            if (data.errors) {
                Object.keys(data.errors).forEach(field => {
                    showFieldError(field, data.errors[field]);
                });
                if (data.focus && document.getElementById(data.focus)) {
                    document.getElementById(data.focus).focus();
                }
            } else {
                // fallback messaggio generico se qualcosa non torna
                alert("Alcuni campi non sono validi. Controlla i messaggi in rosso.");
            }
        } catch (err) {
            // In caso di errore rete, lascia comunque tentare il submit classico
            form.submit();
        }
    });
});
</script>

</body>
</html>
