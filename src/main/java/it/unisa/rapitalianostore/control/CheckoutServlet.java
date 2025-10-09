package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Utente;
import utils.AuthUtils;
import utils.SessionManager;

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

        // 🔒 Controllo login
        if (!AuthUtils.requireLogin(request, response)) return;

        // Aggiungo il token della sessione alla JSP
        request.setAttribute("sessionToken", SessionManager.getSessionToken(request));

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/checkout.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔒 Controllo login e token
        if (!AuthUtils.requireLogin(request, response)) return;
        if (!AuthUtils.requireValidToken(request, response)) return;

        HttpSession session = request.getSession();
        Utente utente = (Utente) session.getAttribute("utente");
        Carrello carrello = (Carrello) session.getAttribute("carrello");

        if (carrello == null || carrello.isVuoto()) {
            request.setAttribute("erroreCheckout", "Il carrello è vuoto.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        String indirizzo = request.getParameter("indirizzo");
        String metodoPagamento = request.getParameter("metodoPagamento");

        // ✅ Controllo campi obbligatori
        if (indirizzo == null || indirizzo.trim().isEmpty() || metodoPagamento == null) {
            request.setAttribute("erroreCheckout", "Compila tutti i campi obbligatori.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        boolean datiValidi = true;

        switch (metodoPagamento) {
            case "carta":
                String numeroCarta = request.getParameter("numeroCarta");
                String scadenza = request.getParameter("scadenza");
                String cvv = request.getParameter("cvv");

                if (numeroCarta == null || !numeroCarta.matches("\\d{16}")) datiValidi = false;
                if (scadenza == null || !scadenza.matches("^(0[1-9]|1[0-2])/\\d{2}$")) datiValidi = false;
                if (cvv == null || !cvv.matches("\\d{3}")) datiValidi = false;
                break;

            case "paypal":
                String paypalEmail = request.getParameter("paypalEmail");
                if (paypalEmail == null || paypalEmail.trim().isEmpty()) datiValidi = false;
                break;

            case "bonifico":
                // Nessun campo extra
                break;

            default:
                datiValidi = false;
        }

        if (!datiValidi) {
            request.setAttribute("erroreCheckout", "I dati di pagamento non sono validi.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        boolean ok = ordineDAO.salvaOrdine(utente, carrello, indirizzo, metodoPagamento);

        if (ok) {
            session.removeAttribute("carrello");
            session.setAttribute("totaleCarrelloHeader", 0.0);
            request.setAttribute("msgSuccesso", "Ordine completato con successo!");
            request.getRequestDispatcher("/WEB-INF/views/confermaOrdine.jsp").forward(request, response);
        } else {
            request.setAttribute("erroreCheckout", "Errore durante il checkout. Riprova.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        }
    }
}
