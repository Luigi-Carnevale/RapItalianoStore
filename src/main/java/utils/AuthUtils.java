package utils;

import java.io.IOException;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import it.unisa.rapitalianostore.model.Utente;
import org.mindrot.jbcrypt.BCrypt;

public class AuthUtils {

    public static boolean isAdmin(Utente utente) {
        return utente != null && "admin".equalsIgnoreCase(utente.getRuolo());
    }

    public static boolean requireLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        Utente u = SessionManager.getUtente(request);
        if (u == null) {
            response.sendRedirect("login.jsp");
            return false;
        }
        return true;
    }

    public static String hashPassword(String plainPassword) {
        return BCrypt.hashpw(plainPassword, BCrypt.gensalt());
    }

    public static boolean verificaPassword(String plainPassword, String hashPassword) {
        return BCrypt.checkpw(plainPassword, hashPassword);
    }
}
