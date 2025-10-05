package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import it.unisa.rapitalianostore.model.Utente;

public class SessionManager {

    // Crea una sessione per l'utente autenticato
    public static void creaSessione(HttpServletRequest request, Utente utente) {
        HttpSession session = request.getSession(true);
        session.setAttribute("utente", utente);
    }

    // Restituisce l'utente loggato, o null se non esiste
    public static Utente getUtente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utente) session.getAttribute("utente");
        }
        return null;
    }

    // Verifica se esiste una sessione attiva
    public static boolean isLogged(HttpServletRequest request) {
        return getUtente(request) != null;
    }

    // Termina la sessione (logout)
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            session.invalidate();
        }
    }
}
