package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import it.unisa.rapitalianostore.model.Utente;

public class SessionManager {

    // Crea una nuova sessione per l'utente
    public static void creaSessione(HttpServletRequest request, Utente utente) {
        HttpSession session = request.getSession(true);
        session.setAttribute("utente", utente);
    }

    // Recupera l'utente loggato dalla sessione
    public static Utente getUtente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utente) session.getAttribute("utente");
        }
        return null;
    }

    // Chiude la sessione (logout)
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }

    // Verifica se l'utente è loggato
    public static boolean isLogged(HttpServletRequest request) {
        return getUtente(request) != null;
    }
}
