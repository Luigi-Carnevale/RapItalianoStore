package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Utente;
import utils.SessionManager;
import utils.AuthUtils;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // Se già loggato → redirect diretto a home o admin
        Utente utente = SessionManager.getUtente(request);
        if (utente != null) {
            if (AuthUtils.isAdmin(utente)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }
            return;
        }

        // Altrimenti mostra la pagina di login
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email != null) email = email.trim();

        Utente utente = utenteDAO.findByEmail(email);

        System.out.println("[Login] email=" + email);
        System.out.println("[Login] utenteFound=" + (utente != null));

        boolean ok = (utente != null) &&
                     AuthUtils.verificaPassword(password, utente.getPassword());

        if (ok) {
            // ✅ Crea la sessione tramite SessionManager
            SessionManager.creaSessione(request, utente);

            System.out.println("[Login] OK ruolo=" + utente.getRuolo());
            if (AuthUtils.isAdmin(utente)) {
                response.sendRedirect(request.getContextPath() + "/admin");
            } else {
                response.sendRedirect(request.getContextPath() + "/home");
            }

        } else {
            System.out.println("[Login] FAIL");
            request.setAttribute("error", "Email o password errati");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}
