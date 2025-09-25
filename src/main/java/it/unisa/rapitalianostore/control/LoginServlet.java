package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {

    private UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
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

        // --- DEBUG LOG ---
        System.out.println("[Login] email=" + email);
        System.out.println("[Login] utenteFound=" + (utente != null));
        if (utente != null && utente.getPassword() != null) {
            System.out.println("[Login] hashLen=" + utente.getPassword().length());
            String h = utente.getPassword();
            System.out.println("[Login] hashPrefix=" + (h.length() >= 7 ? h.substring(0,7) : h));
        }

        boolean ok = (utente != null) && utenteDAO.checkPassword(password, utente.getPassword());

        if (ok) {
            HttpSession session = request.getSession();
            session.setAttribute("utente", utente);
            System.out.println("[Login] OK ruolo=" + utente.getRuolo());
            response.sendRedirect(request.getContextPath() + ( "admin".equals(utente.getRuolo()) ? "/admin" : "/home"));
        } else {
            System.out.println("[Login] FAIL");
            request.setAttribute("error", "Email o password errati");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/login.jsp");
            dispatcher.forward(request, response);
        }
    }
}
