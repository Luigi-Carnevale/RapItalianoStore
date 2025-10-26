document.addEventListener("DOMContentLoaded", () => {
  const hamburger = document.getElementById("hamburger");
  const navMenu   = document.getElementById("nav-menu");
  if (!hamburger || !navMenu) return; // evita errori se gli id non ci sono

  hamburger.addEventListener("click", () => {
    navMenu.classList.toggle("active");        // ← prima era "show"
    document.body.classList.toggle("menu-open", navMenu.classList.contains("active"));
    hamburger.setAttribute("aria-expanded", navMenu.classList.contains("active") ? "true" : "false");
  });

  // chiudi quando clicchi un link del menu (UX mobile)
  navMenu.addEventListener("click", (e) => {
    if (e.target.closest("a")) {
      navMenu.classList.remove("active");
      document.body.classList.remove("menu-open");
      hamburger.setAttribute("aria-expanded", "false");
    }
  });

  // chiudi con ESC
  document.addEventListener("keydown", (e) => {
    if (e.key === "Escape") {
      navMenu.classList.remove("active");
      document.body.classList.remove("menu-open");
      hamburger.setAttribute("aria-expanded", "false");
    }
  });
});
