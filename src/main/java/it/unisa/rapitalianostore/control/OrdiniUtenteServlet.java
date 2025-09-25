package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Ordine;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;
import java.util.List;

@WebServlet("/ordini")
public class OrdiniUtenteServlet extends HttpServlet {

    private final OrdineDAO ordineDAO = new OrdineDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }

        // Recupera ordini con dettagli prodotti
        List<Ordine> ordini = ordineDAO.findByUtenteWithDettagli(utente.getId());

        request.setAttribute("ordini", ordini);
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/ordini.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }
}
