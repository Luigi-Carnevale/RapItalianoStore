package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.control.BaseAdminServlet; 
import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.model.Artista;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@WebServlet("/artistiAdmin")
@MultipartConfig // per upload file
public class ArtistiAdminServlet extends BaseAdminServlet {    

    private final ArtistaDAO artistaDAO = new ArtistaDAO();

    // ---------- GET ----------

    @Override
    protected void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utente u = (Utente) ((request.getSession(false) != null) ? request.getSession(false).getAttribute("utente") : null);
        if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("artisti", artistaDAO.findAll());
        request.getRequestDispatcher("/WEB-INF/views/gestioneArtisti.jsp").forward(request, response);
    }

    @Override
    protected void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/views/formArtista.jsp").forward(request, response);
    }

    @Override
    protected void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID artista mancante.");
            list(request, response);
            return;
        }
        Artista a = artistaDAO.findById(id);
        if (a == null) {
            request.setAttribute("error", "Artista non trovato.");
            list(request, response);
            return;
        }
        request.setAttribute("artista", a);
        request.getRequestDispatcher("/WEB-INF/views/formArtista.jsp").forward(request, response);
    }

    // ---------- POST ----------

    @Override
    protected void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        if (!validCsrf(request)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido"); return; }

        String nome = request.getParameter("nome");
        if (nome == null || nome.isBlank()) {
            request.setAttribute("error", "Nome obbligatorio.");
            showCreateForm(request, response);
            return;
        }
        String genere = request.getParameter("genere");
        String bio    = request.getParameter("bio");
        String fileName = handleUploadOrOld(request, null);

        Artista a = new Artista();
        a.setNome(nome.trim());
        a.setGenere(genere);
        a.setBio(bio);
        a.setImmagine(fileName);

        artistaDAO.save(a);
        response.sendRedirect(request.getContextPath() + "/artistiAdmin");
    }

    @Override
    protected void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        if (!validCsrf(request)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido"); return; }

        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID mancante.");
            list(request, response);
            return;
        }
        Artista a = artistaDAO.findById(id);
        if (a == null) {
            request.setAttribute("error", "Artista non trovato.");
            list(request, response);
            return;
        }

        String nome = request.getParameter("nome");
        String genere = request.getParameter("genere");
        String bio    = request.getParameter("bio");
        String fileName = handleUploadOrOld(request, request.getParameter("oldImage"));

        if (nome == null || nome.isBlank()) {
            request.setAttribute("error", "Nome obbligatorio.");
            showEditForm(request, response);
            return;
        }

        a.setNome(nome.trim());
        a.setGenere(genere);
        a.setBio(bio);
        a.setImmagine(fileName);

        artistaDAO.update(a);
        response.sendRedirect(request.getContextPath() + "/artistiAdmin");
    }

    @Override
    protected void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        if (!validCsrf(request)) { response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido"); return; }

        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID mancante.");
            list(request, response);
            return;
        }
        artistaDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/artistiAdmin");
    }

    // ---------- Helpers ----------

    private boolean validCsrf(HttpServletRequest request) {
        String expected = (String) request.getSession().getAttribute("csrfToken");
        String provided = request.getParameter("csrfToken");
        return expected != null && expected.equals(provided);
    }

    private String handleUploadOrOld(HttpServletRequest request, String old) throws IOException, ServletException {
        Part filePart = request.getPart("immagine");
        if (filePart != null && filePart.getSize() > 0) {
            String fileName = new File(Objects.requireNonNull(filePart.getSubmittedFileName())).getName();
            String uploadPath = request.getServletContext().getRealPath("/assets/img");
            File uploadDir = new File(uploadPath);
            if (!uploadDir.exists()) uploadDir.mkdirs();
            filePart.write(uploadPath + File.separator + fileName);
            return fileName;
        }
        return old;
    }
}
