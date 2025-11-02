/* Script stepper quantità per carrello
   - Gestisce pulsanti +/− e input manuale
   - Aggiorna il totale riga lato client
   - Aggiorna il totale carrello via AJAX su /carrelloAjax
   - Usa window.APP_CTX per il context path
*/

(function () {
  // === Utilità ===
  function clamp(val, min, max) {
    return Math.max(min, Math.min(max, val));
  }

  // formattazione semplice "€ 12.34" coerente con la parte JS
  // (Server-side usi JSTL fmt:formatNumber per le cifre iniziali)
  function formatEuro(n) {
    return "€ " + Number(n).toFixed(2);
  }

  // Aggiornamento DOM del totale riga immediato
  function aggiornaTotaleRiga(idProdotto, quantita, prezzoUnitario) {
    var lineEl = document.getElementById("lineTotal-" + idProdotto);
    if (!lineEl) return;
    var tot = quantita * prezzoUnitario;
    lineEl.textContent = formatEuro(tot);
  }

  // Chiamata AJAX al backend per aggiornare la quantità e ottenere nuovo totale carrello
  function inviaAggiornamento(idProdotto, quantita) {
    var base = (window.APP_CTX || "");
    return fetch(base + "/carrelloAjax", {
      method: "POST",
      headers: { "Content-Type": "application/x-www-form-urlencoded" },
      body:
        "idProdotto=" + encodeURIComponent(idProdotto) +
        "&quantita=" + encodeURIComponent(quantita)
    })
      .then(function (r) { return r.json(); })
      .then(function (data) {
        if (data && data.success) {
          var tot = document.getElementById("totaleCarrello");
          if (tot) tot.textContent = formatEuro(data.totale);
        }
      })
      .catch(function () {
        // mostrare un messaggio d'errore/ toast
      });
  }

  // Inizializzazione di tutti i controlli quantità presenti nella tabella
  function initQtyControls() {
    var controls = document.querySelectorAll(".qty-control");
    controls.forEach(function (wrap) {
      var input = wrap.querySelector(".qty-input");
      var btns = wrap.querySelectorAll(".qty-btn");
      var idProdotto = wrap.getAttribute("data-item-id");
      var prezzoUnit = parseFloat(wrap.getAttribute("data-prezzo"));
      var min = parseInt(input.min || wrap.getAttribute("data-min") || "1", 10);
      var max = parseInt(input.max || wrap.getAttribute("data-max") || "99", 10);

      // Stato UI (disabilita visivamente +/− quando a min/max)
      function updateState() {
        var v = parseInt(input.value || min, 10);
        wrap.classList.toggle("is-min", v <= min);
        wrap.classList.toggle("is-max", v >= max);
      }

      // Normalizza, aggiorna riga, invia AJAX
      function commitChange() {
        var v = parseInt(input.value || min, 10);
        if (isNaN(v)) v = min;
        v = clamp(v, min, max);
        input.value = v;
        aggiornaTotaleRiga(idProdotto, v, prezzoUnit);
        inviaAggiornamento(idProdotto, v);
        updateState();
      }

      // Gestione pulsante "−"
      btns[0].addEventListener("click", function () {
        var v = parseInt(input.value || min, 10);
        if (isNaN(v)) v = min;
        v = clamp(v - 1, min, max);
        if (v != input.value) { input.value = v; commitChange(); }
      });

      // Gestione pulsante "+"
      btns[1].addEventListener("click", function () {
        var v = parseInt(input.value || min, 10);
        if (isNaN(v)) v = min;
        v = clamp(v + 1, min, max);
        if (v != input.value) { input.value = v; commitChange(); }
      });

      // Input manuale (al cambio applico clamp + aggiornamenti)
      input.addEventListener("change", commitChange);

      // Stato iniziale coerente con valore pre-caricato
      updateState();
    });
  }

  // Esecuzione su DOM pronto (lo script è caricato con defer)
  initQtyControls();

  // esporto una funzione globale se serve in altre parti
  window.carrelloAggiornaQuantita = inviaAggiornamento;
})();
