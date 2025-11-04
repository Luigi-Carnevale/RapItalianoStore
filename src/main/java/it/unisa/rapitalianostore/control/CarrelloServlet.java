package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.CarrelloDAO;   
import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Prodotto;
import it.unisa.rapitalianostore.model.Utente;      

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

/**
 * sincronizza DB quando l'utente è loggato (add/remove/update).
 */
@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        // aggiorno il totale in sessione per l’header
        session.setAttribute("totaleCarrelloHeader", carrello.getTotale());

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/carrello.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        ProdottoDAO dao = new ProdottoDAO();
        Utente utente = (Utente) session.getAttribute("utente");    // controlla login
        CarrelloDAO carDao = (utente != null) ? new CarrelloDAO() : null; // usa DAO solo se loggato

        if ("add".equals(action)) {
            try {
                int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
                Prodotto prodotto = dao.findById(idProdotto);
                if (prodotto != null) {
                    carrello.aggiungiProdotto(prodotto);
                    // persisto incremento se loggato
                    if (utente != null) carDao.aggiungiItem(utente.getId(), idProdotto, 1);
                }
            } catch (NumberFormatException ignore) {}

            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());

            // Risposta AJAX
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":true," +
                    "\"count\":" + carrello.getItems().size() + "," +
                    "\"totale\":" + carrello.getTotale() + "}"
                );
                return;
            }

            request.setAttribute("prodotti", dao.findAllWithArtisti());
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/catalogo.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if ("remove".equals(action)) {
            try {
                int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
                carrello.rimuoviProdotto(idProdotto);
                // persisto rimozione se loggato
                if (utente != null) carDao.rimuoviItem(utente.getId(), idProdotto);
            } catch (NumberFormatException ignore) {}

            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        if ("update".equals(action)) {
            try {
                int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
                int quantita = Integer.parseInt(request.getParameter("quantita"));
                quantita = Math.max(1, quantita);                // clamp
                carrello.aggiornaQuantita(idProdotto, quantita);
                // persisto quantità esatta se loggato
                if (utente != null) carDao.aggiornaQuantita(utente.getId(), idProdotto, quantita);
            } catch (NumberFormatException ignore) {}

            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());

            // Risposta AJAX: ritorno almeno il totale carrello aggiornato
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":true," +
                    "\"totaleCarrello\":" + carrello.getTotale() + "}"
                );
                return;
            }

            // fallback classico
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }
}
