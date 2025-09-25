package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.dao.RecensioneDAO;
import it.unisa.rapitalianostore.model.Prodotto;
import it.unisa.rapitalianostore.model.Recensione;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/prodotto")
public class ProdottoServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO = new ProdottoDAO();
    private final RecensioneDAO recensioneDAO = new RecensioneDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String idParam = request.getParameter("id");
        if (idParam == null) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
            return;
        }

        try {
            int id = Integer.parseInt(idParam);

            // Trova prodotto
            Prodotto prodotto = prodottoDAO.findById(id);
            if (prodotto == null) {
                response.sendRedirect(request.getContextPath() + "/catalogo");
                return;
            }

            // Trova recensioni
            List<Recensione> recensioni = recensioneDAO.findByProdotto(id);

            // Passa a JSP
            request.setAttribute("prodotto", prodotto);
            request.setAttribute("recensioni", recensioni);

            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/prodotto.jsp");
            dispatcher.forward(request, response);

        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/catalogo");
        }
    }
}
