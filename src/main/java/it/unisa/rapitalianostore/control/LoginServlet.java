package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.CarrelloDAO;        
import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Carrello;         
import it.unisa.rapitalianostore.model.Utente;
import utils.SessionManager;
import utils.AuthUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;                                     
import java.util.Map;                                     

/**
 * Servlet di login.
 * implementato il merge del carrello guest → DB utente e reload in sessione.
 * prendo lo snapshot del carrello PRIMA di creare la nuova sessione (che invalida la vecchia).
 */
@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se già loggato → redirect diretto
        Utente utente = SessionManager.getUtente(request);
        if (utente != null) {
            if (AuthUtils.isAdmin(utente)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) email = email.trim();

        Utente utente = utenteDAO.findByEmail(email);

        System.out.println("[Login] email=" + email);
        System.out.println("[Login] utenteFound=" + (utente != null));

        boolean ok = (utente != null) &&
                AuthUtils.verificaPassword(password, utente.getPassword());

        if (ok) {

            /* ============================
               snapshot del carrello guest PRIMA di creare la nuova sessione
               (creaSessione può chiamare invalidate(); non possiamo leggere 'carrello' dopo)
               ============================ */
            Map<Integer,Integer> guestSnapshot = null;           
            HttpSession oldSession = request.getSession(false);   // non creare se non esiste
            if (oldSession != null) {                             
                Carrello guest = (Carrello) oldSession.getAttribute("carrello");
                if (guest != null && guest.hasItems()) {
                    guestSnapshot = guest.toMap();                // mappa {idProdotto->quantita}
                }
            }

            // Crea la sessione e il token di sicurezza (probabile invalidate + nuova sessione)
            SessionManager.creaSessione(request, utente);

            /* ============================
               Merge carrello guest → DB usando lo snapshot
               ============================ */
            HttpSession s = request.getSession();                 // nuova sessione
            CarrelloDAO carDao = new CarrelloDAO();

            if (guestSnapshot != null && !guestSnapshot.isEmpty()) { // usa snapshot
                guestSnapshot.forEach((idProdotto, quantita) -> {
                    if (quantita != null && quantita > 0) {
                        carDao.aggiungiItem(utente.getId(), idProdotto, quantita);
                    }
                });
            }

            // 2) Ricarico dal DB l'intero carrello e lo metto in sessione come Carrello "ufficiale"
            List<CarrelloDAO.CarrelloItemDTO> dtos = carDao.caricaItems(utente.getId());
            Carrello merged = new Carrello();
            merged.replaceAllFromDTOs(dtos);
            s.setAttribute("carrello", merged);
            s.setAttribute("totaleCarrelloHeader", merged.getTotale());

            /* ============================
               Supporto al parametro "next"
               - Se il filtro ha passato ?next=..., torna lì dopo il login
               - Safe redirect: accetto solo URL interni (che iniziano con contextPath)
               ============================ */
            String next = request.getParameter("next"); // può essere URL-encoded; ok per sendRedirect
            if (next != null && !next.isEmpty()) {
                String ctx = request.getContextPath();
                if (next.startsWith(ctx)) {
                    response.sendRedirect(next);
                } else {
                    response.sendRedirect(ctx + "/home");
                }
                return;
            }

            System.out.println("[Login] OK ruolo=" + utente.getRuolo());
            if (AuthUtils.isAdmin(utente)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }

        } else {
            System.out.println("[Login] FAIL");
            request.setAttribute("error", "Email o password errati");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}
