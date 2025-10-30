// Filtro che garantisce che l'area admin sia visibile solo agli admin 

package it.unisa.rapitalianostore.filters;

import it.unisa.rapitalianostore.model.Utente;
import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;

public class AdminAuthFilter implements Filter {
  @Override
  public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
      throws IOException, ServletException {
    HttpServletRequest req = (HttpServletRequest) request;
    HttpServletResponse resp = (HttpServletResponse) response;

    HttpSession session = req.getSession(false);
    Utente u = (session != null) ? (Utente) session.getAttribute("utente") : null;

    if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
      resp.sendError(HttpServletResponse.SC_FORBIDDEN); // o redirect a /home
      return;
    }
    chain.doFilter(request, response);
  }
}