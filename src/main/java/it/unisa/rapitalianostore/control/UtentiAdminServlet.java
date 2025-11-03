package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.control.BaseAdminServlet; 
import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/utentiAdmin")
public class UtentiAdminServlet extends BaseAdminServlet {  

    private final UtenteDAO utenteDAO = new UtenteDAO();

    // Lista utenti (GET senza action o fallback)
    @Override
    protected void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        List<Utente> utenti = utenteDAO.findAll();
        request.setAttribute("utenti", utenti);
        request.getRequestDispatcher("/WEB-INF/views/gestioneUtenti.jsp").forward(request, response);
    }

    // Non ho form edit utente; lascio gli hook default.

    // Azioni POST:

    @Override
    protected void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        if (!validCsrf(request)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido"); return; }
        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID mancante.");
            list(request, response);
            return;
        }
        utenteDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/utentiAdmin");
    }

    @Override
    protected void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // userò action=update con subAction=promote/demote
        if (!validCsrf(request)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido"); return; }

        String sub = request.getParameter("subAction");
        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null || sub == null) {
            request.setAttribute("error", "Parametri mancanti.");
            list(request, response);
            return;
        }

        switch (sub) {
            case "promote": utenteDAO.updateRole(id, "admin");   break;
            case "demote":  utenteDAO.updateRole(id, "cliente"); break;
            default:
                request.setAttribute("error", "Azione non riconosciuta.");
                list(request, response);
                return;
        }

        response.sendRedirect(request.getContextPath() + "/utentiAdmin");
    }

    private boolean validCsrf(HttpServletRequest request) {
        String expected = (String) request.getSession().getAttribute("csrfToken");
        String provided = request.getParameter("csrfToken");
        return expected != null && expected.equals(provided);
    }
}
