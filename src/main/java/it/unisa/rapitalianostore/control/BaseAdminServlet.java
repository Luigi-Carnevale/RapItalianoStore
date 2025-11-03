package it.unisa.rapitalianostore.control;

import javax.servlet.http.*;
import javax.servlet.*;
import java.io.IOException;

/**
 * Router base per area admin.
 * - Implementa sia doGet che doPost (evita 405).
 * - Fornisce fallback "list" se manca/è sconosciuta l'azione (evita 400).
 * - Include utility parseIntSafe/trimOrNull.
 * Le servlet admin concrete estendono questa classe e implementano i metodi hook.
 */
public abstract class BaseAdminServlet extends HttpServlet {

    @Override
    protected final void doGet(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        route(req, resp, "GET");
    }

    @Override
    protected final void doPost(HttpServletRequest req, HttpServletResponse resp)
            throws ServletException, IOException {
        route(req, resp, "POST");
    }

    private void route(HttpServletRequest req, HttpServletResponse resp, String method)
            throws ServletException, IOException {

        // non creo logiche di guard qui; già c'è AdminAuthFilter. Le servlet possono ri-verificare.
        String action = trimOrNull(req.getParameter("action"));

        if ("GET".equals(method)) {
            if (action == null) { list(req, resp); return; }
            switch (action) {
                case "new":  showCreateForm(req, resp); return;
                case "edit": showEditForm(req, resp);   return;
                default:     list(req, resp);           return; // fallback gentile
            }
        } else {
            // POST: mutazioni
            if (action == null) {
                req.setAttribute("error", "Azione non specificata.");
                list(req, resp);
                return;
            }
            switch (action) {
                case "create": create(req, resp); return;
                case "update": update(req, resp); return;
                case "delete": delete(req, resp); return;
                default:
                    req.setAttribute("error", "Azione non riconosciuta: " + action);
                    list(req, resp);
            }
        }
    }

    protected String trimOrNull(String s) {
        return s == null ? null : s.trim();
    }

    protected Integer parseIntSafe(String s) {
        try { return (s == null || s.isEmpty()) ? null : Integer.valueOf(s); }
        catch (NumberFormatException e) { return null; }
    }

    // Hook da implementare nelle servlet concrete:
    protected abstract void list(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException;
    protected void showCreateForm(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { list(req, resp); }
    protected void showEditForm  (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { list(req, resp); }
    protected void create        (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { list(req, resp); }
    protected void update        (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { list(req, resp); }
    protected void delete        (HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException { list(req, resp); }
}
