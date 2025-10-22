package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.model.Prodotto;
import it.unisa.rapitalianostore.model.Artista;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;

@WebServlet("/prodottiAdmin")
@MultipartConfig // per gestire upload file
public class ProdottiAdminServlet extends HttpServlet {

    private ProdottoDAO prodottoDAO = new ProdottoDAO();
    private ArtistaDAO artistaDAO = new ArtistaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "addForm":
                request.setAttribute("artisti", artistaDAO.findAll());
                request.getRequestDispatcher("/WEB-INF/views/formProdotto.jsp").forward(request, response);
                break;
            case "edit":
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Prodotto prodotto = prodottoDAO.findById(idEdit);
                request.setAttribute("prodotto", prodotto);
                request.setAttribute("artisti", artistaDAO.findAll());
                request.getRequestDispatcher("/WEB-INF/views/formProdotto.jsp").forward(request, response);
                break;
            case "delete":
                int idDelete = Integer.parseInt(request.getParameter("id"));
                prodottoDAO.delete(idDelete);
                response.sendRedirect(request.getContextPath() + "/prodottiAdmin");
                break;
            default:
                request.setAttribute("prodotti", prodottoDAO.findAllWithArtisti());
                request.getRequestDispatcher("/WEB-INF/views/gestioneProdotti.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String titolo = request.getParameter("titolo");
        String descrizione = request.getParameter("descrizione"); // ✅ nuovo
        double prezzo = Double.parseDouble(request.getParameter("prezzo"));
        int idArtista = Integer.parseInt(request.getParameter("idArtista"));

        // Upload immagine
        Part filePart = request.getPart("immagine");
        String fileName = null;

        if (filePart != null && filePart.getSize() > 0) {
            fileName = new File(filePart.getSubmittedFileName()).getName();

            String uploadPath = getServletContext().getRealPath("/assets/img");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();

            filePart.write(uploadPath + File.separator + fileName);
        } else {
            fileName = request.getParameter("oldImage");
        }

        Prodotto prodotto = new Prodotto();
        prodotto.setTitolo(titolo);
        prodotto.setDescrizione(descrizione); // ✅
        prodotto.setPrezzo(prezzo);
        prodotto.setImmagine(fileName);
        prodotto.setIdArtista(idArtista);

        if (id == null || id.isEmpty()) {
            prodottoDAO.save(prodotto);
        } else {
            prodotto.setId(Integer.parseInt(id));
            prodottoDAO.update(prodotto);
        }

        response.sendRedirect(request.getContextPath() + "/prodottiAdmin");
    }
}
