package utils;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpSession;
import java.util.UUID;
import java.security.SecureRandom;                 // MOD
import java.util.Base64;                          // MOD
import it.unisa.rapitalianostore.model.Utente;

public class SessionManager {

    // MOD: costanti per nomi attributi
    private static final String ATTR_UTENTE      = "utente";
    private static final String ATTR_SESSIONTOK  = "sessionToken";
    private static final String ATTR_CSRF        = "csrfToken"; // MOD

    private static final SecureRandom RNG = new SecureRandom();  // MOD

    // Crea una sessione per l'utente autenticato e genera token
    public static void creaSessione(HttpServletRequest request, Utente utente) {
        // MOD: invalida eventuale sessione precedente per sicurezza
        HttpSession old = request.getSession(false);             // MOD
        if (old != null) old.invalidate();                       // MOD

        HttpSession session = request.getSession(true);
        session.setAttribute(ATTR_UTENTE, utente);

        // ✅ Genera un token di sessione univoco (compatibilità con codice esistente)
        String token = UUID.randomUUID().toString();
        session.setAttribute(ATTR_SESSIONTOK, token);

        // ✅ MOD: Genera/ruota anche il CSRF token (Base64 url-safe)
        byte[] bytes = new byte[32];
        RNG.nextBytes(bytes);
        String csrf = Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
        session.setAttribute(ATTR_CSRF, csrf);

        System.out.println("[SessionManager] Nuova sessione per " 
                + utente.getEmail() + " | sessionToken=" + token + " | csrfToken=" + csrf); // MOD
    }

    // Restituisce l'utente loggato, o null se non esiste
    public static Utente getUtente(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (Utente) session.getAttribute(ATTR_UTENTE);
        }
        return null;
    }

    // Restituisce il token salvato in sessione (compatibilità)
    public static String getSessionToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(ATTR_SESSIONTOK);
        }
        return null;
    }

    // MOD: Getter CSRF, utile per debug/uso mirato
    public static String getCsrfToken(HttpServletRequest request) {   // MOD
        HttpSession session = request.getSession(false);
        if (session != null) {
            return (String) session.getAttribute(ATTR_CSRF);
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
            System.out.println("[SessionManager] Sessione terminata | sessionToken=" 
                    + session.getAttribute(ATTR_SESSIONTOK)
                    + " | csrfToken=" + session.getAttribute(ATTR_CSRF)); // MOD
            session.invalidate();
        }
    }
}
