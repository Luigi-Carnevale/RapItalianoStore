package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ArtistaDAO;                 // per lista artisti (filtro)
import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Ordine;
import it.unisa.rapitalianostore.model.Utente;
import utils.AuthUtils;                                          // per richiedere login
import utils.SessionManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet "I miei ordini" (lato cliente).
 * - Richiede login
 * - Supporta filtri (data inizio, data fine, artista)
 * - Popola la select degli artisti
 */
@WebServlet("/ordini")
public class OrdiniUtenteServlet extends HttpServlet {

    private final OrdineDAO ordineDAO = new OrdineDAO();
    private final ArtistaDAO artistaDAO = new ArtistaDAO();      

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        //  Richiede login; se non loggato reindirizza a /login
        if (!AuthUtils.requireLogin(request, response)) return;

        Utente utente = SessionManager.getUtente(request);

        // Leggo i filtri (tutti opzionali)
        String dataInizio = trimOrNull(request.getParameter("dataInizio"));  // yyyy-MM-dd
        String dataFine   = trimOrNull(request.getParameter("dataFine"));    // yyyy-MM-dd
        String artistaS   = trimOrNull(request.getParameter("idArtista"));
        Integer idArtista = null;
        try {
            if (artistaS != null && !artistaS.isEmpty()) idArtista = Integer.valueOf(artistaS);
        } catch (NumberFormatException ignore) {}

        // Scelgo la query: senza filtri -> tutti gli ordini utente; con filtri -> query dedicata
        boolean hasFilters = (dataInizio != null) || (dataFine != null) || (idArtista != null);
        List<Ordine> ordini = hasFilters
                ? ordineDAO.findByUtenteWithFilters(utente.getId(), dataInizio, dataFine, idArtista) // MOD
                : ordineDAO.findByUtenteWithDettagli(utente.getId());

        // Attributi per la JSP
        request.setAttribute("ordini", ordini);
        request.setAttribute("artisti", artistaDAO.findAll()); // lista per select artista

        // Rimetto i valori dei filtri per mantenerli al refresh
        request.setAttribute("dataInizio", dataInizio);
        request.setAttribute("dataFine", dataFine);
        request.setAttribute("idArtista", idArtista);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/ordini.jsp");
        rd.forward(request, response);
    }

    // Non serve gestire POST qui; eventuali azioni future potranno delegare a doGet.
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String trimOrNull(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
        }
}
