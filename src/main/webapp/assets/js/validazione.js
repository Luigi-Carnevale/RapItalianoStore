document.addEventListener("DOMContentLoaded", () => {
  // Registrazione
  const formReg = document.getElementById("formRegistrazione");
  if (formReg) {
    formReg.addEventListener("submit", (e) => {
      const email = document.getElementById("email");
      const password = document.getElementById("password");
      const emailErr = document.getElementById("erroreEmail");
      const passErr  = document.getElementById("errorePassword");
      if (emailErr) emailErr.textContent = "";
      if (passErr) passErr.textContent = "";

      const emailRegex = /^[^@\s]+@[^@\s]+\.[^@\s]+$/;
      let ok = true;

      if (!emailRegex.test(email.value)) {
        if (emailErr) emailErr.textContent = "Email non valida";
        ok = false;
      }
      if (!password.value || password.value.length < 6) {
        if (passErr) passErr.textContent = "La password deve avere almeno 6 caratteri";
        ok = false;
      }
      if (!ok) e.preventDefault();
    });
  }

  // Checkout
  const formChk = document.getElementById("formCheckout");
  if (formChk) {
    formChk.addEventListener("submit", (e) => {
      const indirizzo = document.getElementById("indirizzo");
      const indErr = document.getElementById("erroreIndirizzo");
      if (indErr) indErr.textContent = "";
      let ok = true;

      if (!indirizzo.value || indirizzo.value.trim().length < 8) {
        if (indErr) indErr.textContent = "Inserisci un indirizzo completo";
        ok = false;
      }
      if (!ok) e.preventDefault();
    });
  }
});
