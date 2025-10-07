package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Prodotto;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

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

        if ("add".equals(action)) {
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            Prodotto prodotto = dao.findById(idProdotto);
            if (prodotto != null) {
                carrello.aggiungiProdotto(prodotto);
            }

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
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            carrello.rimuoviProdotto(idProdotto);
            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());
            response.sendRedirect(request.getContextPath() + "/carrello");
            return;
        }

        if ("update".equals(action)) {
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            int quantita = Integer.parseInt(request.getParameter("quantita"));
            carrello.aggiornaQuantita(idProdotto, quantita);
            session.setAttribute("totaleCarrelloHeader", carrello.getTotale());

            // 🔹 Risposta AJAX con JSON
            if ("XMLHttpRequest".equals(request.getHeader("X-Requested-With"))) {
                double totaleRiga = carrello.getItems().stream()
                        .filter(i -> i.getProdotto().getId() == idProdotto)
                        .mapToDouble(i -> i.getQuantita() * i.getProdotto().getPrezzo())
                        .findFirst()
                        .orElse(0);

                response.setContentType("application/json");
                response.getWriter().write(
                    "{\"success\":true," +
                    "\"totaleRiga\":" + totaleRiga + "," +
                    "\"totaleCarrello\":" + carrello.getTotale() + "}"
                );
                return;
            }

            // fallback classico
            response.sendRedirect(request.getContextPath() + "/carrello");
        }
    }
}
