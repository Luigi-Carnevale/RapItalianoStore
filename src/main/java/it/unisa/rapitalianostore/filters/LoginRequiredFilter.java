package it.unisa.rapitalianostore.filters;

import it.unisa.rapitalianostore.model.Utente;

import javax.servlet.*;
import javax.servlet.http.*;
import java.io.IOException;
import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;

/**
 * MOD: Filtro che protegge le rotte che richiedono login (es. /checkout).
 * Distingue tra:
 *  - sessione DAVVERO scaduta (avevi un JSESSIONID ma non è più valido) -> mostra messaggio
 *  - utente non loggato/guest -> nessun messaggio, redirect pulito al login
 */
public class LoginRequiredFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest  request  = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;

        // getSession(false) per NON crearne una nuova, ci serve capire se è scaduta
        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;

        if (utente == null) {
            String ctx = request.getContextPath();

            // "sessione scaduta" solo se il client presenta un JSESSIONID non valido
            boolean expired = request.getRequestedSessionId() != null
                    && !request.isRequestedSessionIdValid();

            // calcolo la destinazione da riprendere dopo il login
            String target = request.getRequestURI()
                    + (request.getQueryString() != null ? "?" + request.getQueryString() : "");
            String next = URLEncoder.encode(target, StandardCharsets.UTF_8);

            if (expired) {
                // sessione scaduta -> passo un flag esplicito
                response.sendRedirect(ctx + "/login?error=sessionExpired&next=" + next);
            } else {
                // guest/non loggato -> nessun errore, solo next
                response.sendRedirect(ctx + "/login?next=" + next);
            }
            return;
        }

        // Utente loggato -> proseguo
        chain.doFilter(req, res);
    }
}
