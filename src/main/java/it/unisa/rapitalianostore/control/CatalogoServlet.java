package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.model.Artista;
import it.unisa.rapitalianostore.model.Prodotto;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");           // testo ricerca
        String prezzo = request.getParameter("prezzo"); // filtro prezzo
        String artistaId = request.getParameter("artista"); // filtro artista

        ProdottoDAO prodottoDAO = new ProdottoDAO();
        ArtistaDAO artistaDAO = new ArtistaDAO();

        // Se ci sono parametri → ricerca filtrata, altrimenti tutti
        List<Prodotto> prodotti = prodottoDAO.findFiltered(q, prezzo, artistaId);
        List<Artista> artisti = artistaDAO.findAll(); // per popolare la tendina

        request.setAttribute("prodotti", prodotti);
        request.setAttribute("artisti", artisti);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/catalogo.jsp");
        dispatcher.forward(request, response);

        System.out.println("Servlet Catalogo chiamata");
        System.out.println("Prodotti trovati: " + prodotti.size());
    }
}
