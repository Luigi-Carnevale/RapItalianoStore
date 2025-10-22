package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.model.Artista;
import it.unisa.rapitalianostore.model.Prodotto;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.*;

@WebServlet("/search")
public class SearchServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO = new ProdottoDAO();
    private final ArtistaDAO artistaDAO = new ArtistaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");

        List<Prodotto> albumTrovati = new ArrayList<>();
        List<Artista> artistiTrovati = new ArrayList<>();

        if (q != null && !q.isBlank()) {
            // 🔹 Usa SEMPRE la versione con join (findFiltered deve popolare Artista!)
            albumTrovati = prodottoDAO.findFiltered(q, null, null);
            artistiTrovati = artistaDAO.findByNome(q);

            // 🔹 Se trovo artisti → aggiungo i loro album
            for (Artista a : artistiTrovati) {
            	List<Prodotto> albumArtista = prodottoDAO.findFiltered(null, null, String.valueOf(a.getId()));
            	a.setAlbum(albumArtista);
            }
        }

        // 🔹 fallback HTML → JSP
        request.setAttribute("query", q);
        request.setAttribute("albumTrovati", albumTrovati);
        request.setAttribute("artistiTrovati", artistiTrovati);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/search.jsp");
        dispatcher.forward(request, response);
    }
}
