package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.control.BaseAdminServlet;  // estensione base con dispatch per action
import it.unisa.rapitalianostore.dao.ProdottoDAO;
import it.unisa.rapitalianostore.dao.ArtistaDAO;
import it.unisa.rapitalianostore.model.Prodotto;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.MultipartConfig;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.File;
import java.io.IOException;
import java.util.Objects;

@WebServlet("/prodottiAdmin")
@MultipartConfig // per upload file
public class ProdottiAdminServlet extends BaseAdminServlet {   // estende BaseAdminServlet

    private final ProdottoDAO prodottoDAO = new ProdottoDAO();
    private final ArtistaDAO artistaDAO = new ArtistaDAO();

    // ---------- GET ----------

    @Override
    protected void list(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // doppia guardia (AdminAuthFilter + qui) per redirect pulito
        Utente u = (Utente) ((request.getSession(false) != null) ? request.getSession(false).getAttribute("utente") : null);
        if (u == null || !"admin".equalsIgnoreCase(u.getRuolo())) {
            response.sendRedirect(request.getContextPath() + "/home");
            return;
        }

        request.setAttribute("prodotti", prodottoDAO.findAllWithArtisti()); // DAO restituisce ASC
        request.getRequestDispatcher("/WEB-INF/views/gestioneProdotti.jsp").forward(request, response);
    }

    @Override
    protected void showCreateForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.setAttribute("artisti", artistaDAO.findAll()); // lista artisti ASC
        request.getRequestDispatcher("/WEB-INF/views/formProdotto.jsp").forward(request, response);
    }

    @Override
    protected void showEditForm(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID prodotto mancante.");
            list(request, response);
            return;
        }
        Prodotto prodotto = prodottoDAO.findById(id);
        if (prodotto == null) {
            request.setAttribute("error", "Prodotto non trovato.");
            list(request, response);
            return;
        }
        request.setAttribute("prodotto", prodotto);
        request.setAttribute("artisti", artistaDAO.findAll()); 
        request.getRequestDispatcher("/WEB-INF/views/formProdotto.jsp").forward(request, response);
    }

    // ---------- POST ----------

    @Override
    protected void create(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // CSRF
        if (!validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido");
            return;
        }

        String titolo = request.getParameter("titolo");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");
        Integer idArtista = parseIntSafe(request.getParameter("idArtista")); 

        if (titolo == null || titolo.isBlank() || prezzoStr == null) {
            request.setAttribute("error", "Compila i campi obbligatori.");
            showCreateForm(request, response);
            return;
        }

        double prezzo;
        try {
            prezzo = Double.parseDouble(prezzoStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Prezzo non valido.");
            showCreateForm(request, response);
            return;
        }

        String fileName = handleUploadOrOld(request, null); // gestisce upload o nessuna immagine

        Prodotto p = new Prodotto();
        p.setTitolo(titolo.trim());
        p.setDescrizione(descrizione);
        p.setPrezzo(prezzo);
        p.setImmagine(fileName);
        if (idArtista != null) p.setIdArtista(idArtista);

        prodottoDAO.save(p); // DAO gestisce id_artista null con setNull
        response.sendRedirect(request.getContextPath() + "/prodottiAdmin");
    }

    @Override
    protected void update(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");

        // CSRF
        if (!validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido");
            return;
        }

        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID mancante.");
            list(request, response);
            return;
        }

        Prodotto existing = prodottoDAO.findById(id);
        if (existing == null) {
            request.setAttribute("error", "Prodotto non trovato.");
            list(request, response);
            return;
        }

        String titolo = request.getParameter("titolo");
        String descrizione = request.getParameter("descrizione");
        String prezzoStr = request.getParameter("prezzo");
        Integer idArtista = parseIntSafe(request.getParameter("idArtista"));

        if (titolo == null || titolo.isBlank() || prezzoStr == null) {
            request.setAttribute("error", "Compila i campi obbligatori.");
            showEditForm(request, response);
            return;
        }

        double prezzo;
        try {
            prezzo = Double.parseDouble(prezzoStr);
        } catch (NumberFormatException e) {
            request.setAttribute("error", "Prezzo non valido.");
            showEditForm(request, response);
            return;
        }

        String fileName = handleUploadOrOld(request, request.getParameter("oldImage")); // riusa immagine

        existing.setTitolo(titolo.trim());
        existing.setDescrizione(descrizione);
        existing.setPrezzo(prezzo);
        existing.setImmagine(fileName);
        if (idArtista != null) existing.setIdArtista(idArtista);

        prodottoDAO.update(existing);
        response.sendRedirect(request.getContextPath() + "/prodottiAdmin");
    }

    @Override
    protected void delete(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // CSRF
        if (!validCsrf(request)) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "CSRF token non valido");
            return;
        }

        Integer id = parseIntSafe(request.getParameter("id"));
        if (id == null) {
            request.setAttribute("error", "ID mancante.");
            list(request, response);
            return;
        }

        prodottoDAO.delete(id);
        response.sendRedirect(request.getContextPath() + "/prodottiAdmin");
    }

    // ---------- Helpers ----------

    private boolean validCsrf(HttpServletRequest request) { // helper CSRF
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
        return old; // se non carichi, riusa oldImage
    }
}
