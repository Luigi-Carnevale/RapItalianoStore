package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import java.io.IOException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

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

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/checkout.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login.jsp");
            return;
        }
        if (carrello == null || carrello.isVuoto()) {
            session.setAttribute("msgErrore", "Il carrello è vuoto.");
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        String indirizzo = request.getParameter("indirizzo");
        String metodoPagamento = request.getParameter("metodoPagamento");

        boolean ok = ordineDAO.salvaOrdine(utente, carrello, indirizzo, metodoPagamento);

        if (ok) {
            // ✅ solo dopo il salvataggio svuoto il carrello
            session.removeAttribute("carrello");
            session.setAttribute("totaleCarrelloHeader", 0.0);

            request.setAttribute("msgSuccesso", "Ordine completato con successo!");
            request.getRequestDispatcher("/WEB-INF/views/confermaOrdine.jsp").forward(request, response);
        } else {
            session.setAttribute("msgErrore", "Errore durante il checkout.");
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }
}
