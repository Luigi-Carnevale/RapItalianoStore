package utils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
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
            // JSP protetta sotto WEB-INF, quindi redirect a servlet di login
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
