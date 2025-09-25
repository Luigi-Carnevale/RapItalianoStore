package it.unisa.rapitalianostore.control;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession(false); // non crea nuova sessione
        if (session != null) {
            session.invalidate(); // distrugge la sessione
        }
        response.sendRedirect(request.getContextPath() + "/home"); // torna alla home
    }
}
