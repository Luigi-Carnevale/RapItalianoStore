package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/utentiAdmin")
public class UtentiAdminServlet extends HttpServlet {

    private UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");

        if (action == null) {
            List<Utente> utenti = utenteDAO.findAll();
            request.setAttribute("utenti", utenti);
            request.getRequestDispatcher("/WEB-INF/views/gestioneUtenti.jsp").forward(request, response);
            return;
        }

        int id = Integer.parseInt(request.getParameter("id"));

        switch (action) {
            case "delete":
                utenteDAO.delete(id);
                break;
            case "promote":
                utenteDAO.updateRole(id, "admin");
                break;
            case "demote":
                utenteDAO.updateRole(id, "cliente");
                break;
        }

        response.sendRedirect(request.getContextPath() + "/utentiAdmin");
    }
}
