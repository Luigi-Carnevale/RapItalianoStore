package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Ordine;

import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import javax.servlet.RequestDispatcher;
import java.io.IOException;
import java.util.List;

@WebServlet("/ordiniAdmin")
public class OrdiniAdminServlet extends HttpServlet {

    private final OrdineDAO ordineDAO = new OrdineDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // Leggo i parametri dalla form (stessi name della tua JSP)
        String dataInizioRaw = request.getParameter("dataInizio");
        String dataFineRaw   = request.getParameter("dataFine");
        String emailRaw      = request.getParameter("clienteEmail");

        // Normalizzo: trim e vuoti -> null
        String dataInizio = normalize(dataInizioRaw);
        String dataFine   = normalize(dataFineRaw);
        String clienteEmail = normalize(emailRaw);

        boolean hasFilters =
                (dataInizio != null && !dataInizio.isEmpty()) ||
                (dataFine   != null && !dataFine.isEmpty())   ||
                (clienteEmail != null && !clienteEmail.isEmpty());

        List<Ordine> ordini;
        if (hasFilters) {
            // Usa i filtri (in OrdineDAO: LIKE + LOWER su email)
            ordini = ordineDAO.findByFilter(dataInizio, dataFine, clienteEmail);
        } else {
            // Nessun filtro -> tutti gli ordini
            ordini = ordineDAO.findAll();
        }

        // Rimetto i parametri per mantenerli valorizzati in pagina
        request.setAttribute("ordini", ordini);
        request.setAttribute("dataInizio", dataInizioRaw);
        request.setAttribute("dataFine", dataFineRaw);
        request.setAttribute("clienteEmail", emailRaw);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/ordiniAdmin.jsp");
        rd.forward(request, response);
    }

    // Se in futuro userai POST per i filtri, manteniamo lo stesso flusso
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        doGet(request, response);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
