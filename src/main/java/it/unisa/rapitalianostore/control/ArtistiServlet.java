package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.model.Artista;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.List;

@WebServlet("/artisti")
public class ArtistiServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String q = request.getParameter("q");

        ArtistaDAO dao = new ArtistaDAO();
        List<Artista> artisti;

        if (q != null && !q.isBlank()) {
            artisti = dao.findByNome(q);   // ricerca filtrata
        } else {
            artisti = dao.findAll();       // tutti gli artisti
        }

        request.setAttribute("artisti", artisti);

        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/artisti.jsp");
        dispatcher.forward(request, response);

        System.out.println("Servlet Artisti chiamata");
        System.out.println("Artisti trovati: " + artisti.size());
    }
}
