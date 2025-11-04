package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.CarrelloDAO;       // MOD (opzionale)
import it.unisa.rapitalianostore.model.Carrello;        // MOD (opzionale)
import it.unisa.rapitalianostore.model.Utente;          // MOD (opzionale)
import utils.SessionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        /* MOD (opzionale): se vuoi fare uno snapshot finale forzato.
           In teoria non serve, perché persistiamo ad ogni modifica.
           Lo lascio come commento pronto all’uso.

        HttpSession s = request.getSession(false);
        if (s != null) {
            Utente u = (Utente) s.getAttribute("utente");
            Carrello cart = (Carrello) s.getAttribute("carrello");
            if (u != null && cart != null) {
                new CarrelloDAO().replaceAll(u.getId(), cart.toMap());
            }
        }
        */

        // ✅ Usa SessionManager per gestire logout + token
        SessionManager.logout(request);
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
