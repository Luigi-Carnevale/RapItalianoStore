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

        // nuovi parametri
        String email    = request.getParameter("email");
        String username = request.getParameter("username");    
        String password = request.getParameter("password");
        String conferma = request.getParameter("conferma");    

        if (email != null) email = email.trim();
        if (username != null) username = username.trim();

        UtenteDAO dao = new UtenteDAO();

        // validazioni semplici lato server
        if (email == null || email.isEmpty()
                || username == null || username.isEmpty()
                || password == null || password.isEmpty()
                || conferma == null || conferma.isEmpty()) {
            request.setAttribute("error", "Compila tutti i campi obbligatori.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
            return;
        }

        if (!password.equals(conferma)) { 
            request.setAttribute("error", "Le password non coincidono.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // vincoli di unicità
        if (dao.existsByEmail(email)) {
            request.setAttribute("error", "Email già registrata.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
            return;
        }
        if (dao.existsByUsername(username)) {
            request.setAttribute("error", "Username non disponibile.");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
            return;
        }

        // Hash password
        String hashedPassword = dao.hashPassword(password);

        // Prepara oggetto utente
        Utente nuovo = new Utente();
        nuovo.setEmail(email);
        nuovo.setUsername(username);   
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
            request.setAttribute("error", "Errore nella registrazione (email o username già in uso).");
            RequestDispatcher dispatcher = request.getRequestDispatcher("/WEB-INF/views/registrazione.jsp");
            dispatcher.forward(request, response);
        }
    }
}
