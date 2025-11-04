package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Ordine;
import it.unisa.rapitalianostore.model.Utente;

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

        // guardia "dolce" con redirect (AdminAuthFilter fa già 403 se serve)
        HttpSession s = request.getSession(false);
        Utente u = (s != null) ? (Utente) s.getAttribute("utente") : null;
        if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setCharacterEncoding("UTF-8");

        String dataInizioRaw = request.getParameter("dataInizio");
        String dataFineRaw   = request.getParameter("dataFine");
        String emailRaw      = request.getParameter("clienteEmail");

        String dataInizio = normalize(dataInizioRaw);
        String dataFine   = normalize(dataFineRaw);
        String clienteEmail = normalize(emailRaw);

        boolean hasFilters = (dataInizio != null) || (dataFine != null) || (clienteEmail != null);

        List<Ordine> ordini = hasFilters
                ? ordineDAO.findByFilter(dataInizio, dataFine, clienteEmail)
                : ordineDAO.findAll();

        request.setAttribute("ordini", ordini);
        request.setAttribute("dataInizio", dataInizioRaw);
        request.setAttribute("dataFine", dataFineRaw);
        request.setAttribute("clienteEmail", emailRaw);

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/gestioneOrdini.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // per ora POST reindirizza al flusso GET (filtri). Nessuna azione distruttiva qui.
        doGet(request, response);
    }

    private String normalize(String s) {
        if (s == null) return null;
        String t = s.trim();
        return t.isEmpty() ? null : t;
    }
}
