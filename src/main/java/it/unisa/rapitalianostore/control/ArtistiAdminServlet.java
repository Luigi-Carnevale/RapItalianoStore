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

        /* guardia admin lato server in doGet */
        HttpSession s = request.getSession(false);
        it.unisa.rapitalianostore.model.Utente u =
                (s != null) ? (it.unisa.rapitalianostore.model.Utente) s.getAttribute("utente") : null;
        if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

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

            /* risposta 405 se qualcuno prova a cancellare via GET */
            case "delete":
                response.sendError(HttpServletResponse.SC_METHOD_NOT_ALLOWED, "Usa POST per cancellare");
                return;

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

        /* guardia admin lato server in doPost */
        HttpSession s = request.getSession(false);
        it.unisa.rapitalianostore.model.Utente u =
                (s != null) ? (it.unisa.rapitalianostore.model.Utente) s.getAttribute("utente") : null;
        if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
            response.sendError(HttpServletResponse.SC_FORBIDDEN);
            return;
        }

        /* verifica CSRF token su POST */
        String expected = (String) request.getSession().getAttribute("csrfToken");
        String provided = request.getParameter("csrfToken");
        if (expected == null || !expected.equals(provided)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido");
            return;
        }

        /* branching per azioni POST (delete | save/update) */
        String action = request.getParameter("action");
        if ("delete".equals(action)) {
            // Cancellazione SICURA via POST + CSRF
            String idStr = request.getParameter("id");
            if (idStr == null) {
                response.sendError(HttpServletResponse.SC_BAD_REQUEST, "Id mancante");
                return;
            }
            int idDelete = Integer.parseInt(idStr);
            artistaDAO.delete(idDelete);
            response.sendRedirect(request.getContextPath() + "/artistiAdmin");
            return;
        }

        // Se non è delete, allora è create/update
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
