package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.model.Artista;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;

@WebServlet("/artistiAdmin")
@MultipartConfig // per gestire upload file
public class ArtistiAdminServlet extends HttpServlet {

    private ArtistaDAO artistaDAO = new ArtistaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String action = request.getParameter("action");
        if (action == null) action = "list";

        switch (action) {
            case "addForm":
                request.getRequestDispatcher("/WEB-INF/views/formArtista.jsp").forward(request, response);
                break;
            case "edit":
                int idEdit = Integer.parseInt(request.getParameter("id"));
                Artista artista = artistaDAO.findById(idEdit);
                request.setAttribute("artista", artista);
                request.getRequestDispatcher("/WEB-INF/views/formArtista.jsp").forward(request, response);
                break;
            case "delete":
                int idDelete = Integer.parseInt(request.getParameter("id"));
                artistaDAO.delete(idDelete);
                response.sendRedirect(request.getContextPath() + "/artistiAdmin");
                break;
            default:
                request.setAttribute("artisti", artistaDAO.findAll());
                request.getRequestDispatcher("/WEB-INF/views/gestioneArtisti.jsp").forward(request, response);
                break;
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔴 Importante per accenti/apostrofi
        request.setCharacterEncoding("UTF-8");

        String id = request.getParameter("id");
        String nome = request.getParameter("nome");
        String genere = request.getParameter("genere");
        String bio = request.getParameter("bio");

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

        Artista artista = new Artista();
        artista.setNome(nome);
        artista.setGenere(genere);
        artista.setBio(bio);
        artista.setImmagine(fileName);

        if (id == null || id.isEmpty()) {
            artistaDAO.save(artista);
        } else {
            artista.setId(Integer.parseInt(id));
            artistaDAO.update(artista);
        }

        response.sendRedirect(request.getContextPath() + "/artistiAdmin");
    }
}
