// Validazione AJAX per checkout (compatibile ES5 / Eclipse)
(function () {
  var form = document.getElementById('checkoutForm');
  var submitBtn = document.getElementById('btnCheckout');
  if (!form || !submitBtn) return;

  // Campi da validare
  var fields = Array.prototype.slice.call(form.querySelectorAll('[data-field]'));

  // Stato validazione per abilitare il submit (name -> boolean)
  var validState = {};

  function setHint(name, ok, msg) {
    var hint = document.getElementById('hint-' + name);
    if (!hint) return;
    hint.textContent = msg ? msg : (ok ? 'OK' : '');
    if (hint.classList) {
      hint.classList.toggle('ok', !!ok);
      hint.classList.toggle('err', !ok);
    }
  }

  function toggleSubmit() {
    var allOk = true;
    for (var i = 0; i < fields.length; i++) {
      var f = fields[i];
      if (validState[f.name] !== true) {
        allOk = false;
        break;
      }
    }
    submitBtn.disabled = !allOk;
  }

  function validateField(name, value) {
    var url = form.action
      + '?action=validate'
      + '&field=' + encodeURIComponent(name)
      + '&value=' + encodeURIComponent(value);

    return fetch(url, {
      method: 'GET',
      headers: { 'X-Requested-With': 'XMLHttpRequest' }
    })
    .then(function (res) {
      if (!res.ok) throw new Error('network');
      return res.json();
    })
    .then(function (data) {
      setHint(name, !!data.ok, data.message || '');
      validState[name] = !!data.ok;
    })
    .catch(function () {
      setHint(name, false, 'Errore di rete');
      validState[name] = false;
    })
    .then(function () {
      toggleSubmit();
    });
  }

  // Inizializza stato
  for (var i = 0; i < fields.length; i++) {
    validState[fields[i].name] = false;
  }
  toggleSubmit();

  function makeHandler(field) {
    return function () {
      var val = (field.value || '').trim();
      validateField(field.name, val);
    };
  }

  for (var j = 0; j < fields.length; j++) {
    var f = fields[j];
    var handler = makeHandler(f);
    f.addEventListener('blur', handler);
    f.addEventListener('input', handler);
  }

  form.addEventListener('submit', function (e) {
    var allOk = true;
    for (var k = 0; k < fields.length; k++) {
      if (validState[fields[k].name] !== true) {
        allOk = false;
        break;
      }
    }
    if (!allOk) {
      e.preventDefault();
      for (var h = 0; h < fields.length; h++) {
        validateField(fields[h].name, (fields[h].value || '').trim());
      }
    }
  });
})();
