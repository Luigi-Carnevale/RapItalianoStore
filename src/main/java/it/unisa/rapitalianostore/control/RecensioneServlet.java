package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.RecensioneDAO;
import it.unisa.rapitalianostore.model.Recensione;
import it.unisa.rapitalianostore.model.Utente;
import it.unisa.rapitalianostore.model.Prodotto;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/recensioni")
public class RecensioneServlet extends HttpServlet {

    private final RecensioneDAO recensioneDAO = new RecensioneDAO();

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔹 Verifica che l’utente sia loggato
        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        if (utente == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        try {
            int idProdotto = Integer.parseInt(request.getParameter("idProdotto"));
            int valutazione = Integer.parseInt(request.getParameter("valutazione"));
            String commento = request.getParameter("commento");

            // 🔹 Costruzione recensione
            Recensione recensione = new Recensione();
            recensione.setValutazione(valutazione);   // ✅ usa valutazione
            recensione.setCommento(commento);

            Prodotto prodotto = new Prodotto();
            prodotto.setId(idProdotto);
            recensione.setProdotto(prodotto);

            recensione.setUtente(utente);

            // 🔹 Salvataggio in DB
            recensioneDAO.save(recensione);

            // 🔹 Redirect alla pagina prodotto
            response.sendRedirect(request.getContextPath() + "/prodotto?id=" + idProdotto);

        } catch (NumberFormatException e) {
            // id non valido → torno al catalogo
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }
}
