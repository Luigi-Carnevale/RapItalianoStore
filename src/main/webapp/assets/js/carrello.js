function aggiornaQuantita(idProdotto, quantita, prezzoUnitario) {
  if (quantita < 1) quantita = 1;

  // Aggiorna totale riga subito
  let lineTotal = (quantita * prezzoUnitario).toFixed(2);
  document.getElementById("lineTotal-" + idProdotto).innerText = "€ " + lineTotal;

  // Aggiorna totale carrello via AJAX
  fetch("carrelloAjax", {
    method: "POST",
    headers: { "Content-Type": "application/x-www-form-urlencoded" },
    body: "idProdotto=" + encodeURIComponent(idProdotto) + "&quantita=" + encodeURIComponent(quantita)
  })
    .then(r => r.json())
    .then(data => {
      if (data.success) {
        let tot = document.getElementById("totaleCarrello");
        if (tot) tot.textContent = "€ " + data.totale.toFixed(2);
      }
    })
    .catch(() => {});
}
