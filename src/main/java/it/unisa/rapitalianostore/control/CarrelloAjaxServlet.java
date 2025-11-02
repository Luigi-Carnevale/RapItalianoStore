package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.CarrelloDAO;             
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Utente;                 

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/carrelloAjax")
public class CarrelloAjaxServlet extends HttpServlet {

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws IOException, ServletException {

        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Carrello non trovato\"}");
            return;
        }

        try {
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            int quantita   = Integer.parseInt(request.getParameter("quantita"));

            // normalizzo le quantità lato sessione
            quantita = Math.max(1, quantita);

            carrello.aggiornaQuantita(idProdotto, quantita);
            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());

            // persisto se l'utente è loggato
            Utente utente = (Utente) session.getAttribute("utente");
            if (utente != null) {
                new CarrelloDAO().aggiornaQuantita(utente.getId(), idProdotto, quantita);
            }

            response.setContentType("application/json");
            response.getWriter().write("{\"success\":true,\"totale\":" + carrello.getTotale() + "}");
        } catch (NumberFormatException e) {
            response.setStatus(HttpServletResponse.SC_BAD_REQUEST);
            response.setContentType("application/json");
            response.getWriter().write("{\"success\":false,\"message\":\"Parametri non validi\"}");
        }
    }
}
