package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import it.unisa.rapitalianostore.model.Utente;

public class SessionManager {

    // Crea una sessione per l'utente autenticato e genera token
    public static void creaSessione(HttpServletRequest request, Utente utente) {
        HttpSession session = request.getSession(true);
        session.setAttribute("utente", utente);

        // ✅ Genera un token di sessione univoco
        String token = UUID.randomUUID().toString();
        session.setAttribute("sessionToken", token);

        System.out.println("[SessionManager] Nuova sessione creata per " 
                + utente.getEmail() + " | Token: " + token);
    }

    // Restituisce l'utente loggato, o null se non esiste
    public static Utente getUtente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utente) session.getAttribute("utente");
        }
        return null;
    }

    // Restituisce il token salvato in sessione
    public static String getSessionToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute("sessionToken");
        }
        return null;
    }

    // Verifica se l'utente è loggato
    public static boolean isLogged(HttpServletRequest request) {
        return getUtente(request) != null;
    }

    // Termina la sessione (logout)
    public static void logout(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            System.out.println("[SessionManager] Sessione terminata per token: "
                    + session.getAttribute("sessionToken"));
            session.invalidate();
        }
    }
}
