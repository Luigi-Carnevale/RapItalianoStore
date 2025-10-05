package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.UtenteDAO;
import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        // Mostra la pagina registrazione
        RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
        dispatcher.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        String email = request.getParameter("email");
        String password = request.getParameter("password");

        Utente nuovo = new Utente();
        nuovo.setEmail(email);

        UtenteDAO dao = new UtenteDAO();

        // Hash password
        String hashedPassword = dao.hashPassword(password);
        nuovo.setPassword(hashedPassword);
        nuovo.setRuolo("cliente");

        boolean success = dao.save(nuovo);

        if (success) {
            // salvo messaggio in sessione per mostrarlo nella login
            HttpSession session = request.getSession();
            session.setAttribute("msgRegistrazione", "Registrazione completata! Inserisci le credenziali appena create.");
            
            // redirect alla servlet login
            response.sendRedirect(request.getContextPath() + "/login");
        } else {
            request.setAttribute("error", "Errore nella registrazione");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
        }
    }
}

