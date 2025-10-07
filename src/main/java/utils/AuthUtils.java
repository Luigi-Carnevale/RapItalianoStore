package utils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;
import it.unisa.rapitalianostore.model.Utente;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtils {

    // Controlla se l'utente è admin
    public static boolean isAdmin(Utente utente) {
        return utente != null && "admin".equalsIgnoreCase(utente.getRuolo());
    }

    // Controlla se l'utente è autenticato, altrimenti reindirizza al login
    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response)
            throws IOException {

        Utente u = SessionManager.getUtente(request);
        if (u == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    // ✅ Verifica che il token della richiesta corrisponda a quello della sessione
    public static boolean verificaToken(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session == null) return false;

        String tokenSessione = (String) session.getAttribute("sessionToken");
        String tokenRichiesta = request.getParameter("sessionToken");

        if (tokenSessione == null || tokenRichiesta == null) return false;
        return tokenSessione.equals(tokenRichiesta);
    }

    // ✅ Versione per servlet che vogliono bloccare accessi con token errato
    public static boolean requireValidToken(HttpServletRequest request, HttpServletResponse response)
            throws IOException {
        if (!verificaToken(request)) {
            response.sendRedirect(request.getContextPath() + "/login");
            return false;
        }
        return true;
    }

    // Genera hash sicuro per password
    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    // Verifica password
    public static boolean verificaPassword(String plainPassword, String hashPassword) {
        return BCrypt.checkpw(plainPassword, hashPassword);
    }
}
