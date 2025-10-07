package it.unisa.rapitalianostore.control;

import utils.SessionManager;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/logout")
public class LogoutServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ✅ Usa SessionManager per gestire logout + token
        SessionManager.logout(request);
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
