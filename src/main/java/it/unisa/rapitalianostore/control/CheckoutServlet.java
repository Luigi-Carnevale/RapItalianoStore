package it.unisa.rapitalianostore.control;

import it.unisa.rapitalianostore.dao.OrdineDAO;
import it.unisa.rapitalianostore.model.Carrello;
import it.unisa.rapitalianostore.model.Utente;
import utils.AuthUtils;
import utils.SessionManager;

import javax.servlet.RequestDispatcher;
import javax.servlet.ServletException;
import javax.servlet.annotation.WebServlet;
import javax.servlet.http.*;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.regex.Pattern;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {

    private final OrdineDAO ordineDAO = new OrdineDAO();

    // ==============================
    // Regex di validazione server-side
    // ==============================
    private static final Pattern P_TXT = Pattern.compile("^[A-Za-zÀ-ÖØ-öø-ÿ\\s\\-'\\.]{2,}$");
    private static final Pattern P_ADDR = Pattern.compile("^[0-9A-Za-zÀ-ÖØ-öø-ÿ\\s\\-'\\.,/]{3,}$");
    private static final Pattern P_CAP  = Pattern.compile("^[0-9A-Za-z]{2,10}$");
    private static final Pattern P_PROV = Pattern.compile("^[A-Za-z]{2}$");
    private static final Pattern P_TEL  = Pattern.compile("^[+0-9\\s\\-()]{7,20}$");
    private static final Pattern P_CC   = Pattern.compile("^\\d{16}$");
    private static final Pattern P_CVV  = Pattern.compile("^\\d{3}$");
    private static final Pattern P_SCAD = Pattern.compile("^(0[1-9]|1[0-2])/\\d{2}$");
    private static final Pattern P_MAIL = Pattern.compile("^[^\\s@]+@[^\\s@]+\\.[^\\s@]+$");

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔒 Controllo login
        if (!AuthUtils.requireLogin(request, response)) return;

        // Passo il token sessione alla JSP
        request.setAttribute("sessionToken", SessionManager.getSessionToken(request));

        RequestDispatcher rd = request.getRequestDispatcher("/WEB-INF/views/checkout.jsp");
        rd.forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // 🔒 Controllo login
        if (!AuthUtils.requireLogin(request, response)) return;

        // ====== RAMO AJAX ======
        if ("1".equals(request.getParameter("ajax"))) {
            String mode = request.getParameter("mode");
            if ("all".equals(mode)) {
                // ✅ Validazione COMPLETA di tutti i campi (per submit)
                handleAjaxValidateAll(request, response);
                return;
            } else {
                // ✅ Validazione campo singolo (digitazione live)
                handleAjaxValidation(request, response);
                return;
            }
        }

        // ====== SUBMIT NORMALE (salvataggio ordine) ======
        if (!AuthUtils.requireValidToken(request, response)) return;

        HttpSession session = request.getSession(false);
        Utente utente = (session != null) ? (Utente) session.getAttribute("utente") : null;
        Carrello carrello = (session != null) ? (Carrello) session.getAttribute("carrello") : null;

        if (carrello == null || carrello.isVuoto()) {
            request.setAttribute("erroreCheckout", "Il carrello è vuoto.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        // Lettura campi
        String nome      = trim(request.getParameter("nome"));
        String cognome   = trim(request.getParameter("cognome"));
        String indirizzo = trim(request.getParameter("indirizzo"));
        String cap       = trim(request.getParameter("cap"));
        String citta     = trim(request.getParameter("citta"));
        String provincia = trim(request.getParameter("provincia"));
        String paese     = trim(request.getParameter("paese"));
        String telefono  = trim(request.getParameter("telefono"));
        String metodo    = trim(request.getParameter("metodoPagamento"));

        // Validazione completa (stessa usata nel ramo AJAX)
        Map<String,String> errors = validateAll(nome,cognome,indirizzo,cap,citta,provincia,paese,telefono,metodo,
                                                trim(request.getParameter("numeroCarta")),
                                                trim(request.getParameter("scadenza")),
                                                trim(request.getParameter("cvv")),
                                                trim(request.getParameter("paypalEmail")));
        if (!errors.isEmpty()) {
            // Mostra errore generale; i dettagli arriveranno lato JS nella versione AJAX
            request.setAttribute("erroreCheckout", "Alcuni campi non sono validi.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
            return;
        }

        // Composizione indirizzo unico (schema DB attuale)
        String indirizzoFull = String.format(
            "%s %s, %s, %s %s (%s), %s. Tel: %s",
            nonNull(nome), nonNull(cognome), nonNull(indirizzo),
            nonNull(cap), nonNull(citta), nonNull(provincia), nonNull(paese), nonNull(telefono)
        ).trim();

        boolean ok = ordineDAO.salvaOrdine(utente, carrello, indirizzoFull, metodo);
        if (ok) {
            session.removeAttribute("carrello");
            session.setAttribute("totaleCarrelloHeader", 0.0);

            request.setAttribute("msgSuccesso", "Ordine completato con successo!");
            request.getRequestDispatcher("/WEB-INF/views/confermaOrdine.jsp").forward(request, response);
        } else {
            request.setAttribute("erroreCheckout", "Errore durante il checkout. Riprova.");
            request.getRequestDispatcher("/WEB-INF/views/checkout.jsp").forward(request, response);
        }
    }

    /* ===========================================================
       AJAX: validazione TUTTI I CAMPI (usata al click su "Conferma")
       Risposta: { ok:true }  OPPURE  { ok:false, errors:{field:msg,...}, focus:"fieldId" }
       =========================================================== */
    private void handleAjaxValidateAll(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String nome      = trim(request.getParameter("nome"));
        String cognome   = trim(request.getParameter("cognome"));
        String indirizzo = trim(request.getParameter("indirizzo"));
        String cap       = trim(request.getParameter("cap"));
        String citta     = trim(request.getParameter("citta"));
        String provincia = trim(request.getParameter("provincia"));
        String paese     = trim(request.getParameter("paese"));
        String telefono  = trim(request.getParameter("telefono"));
        String metodo    = trim(request.getParameter("metodoPagamento"));

        String numeroCarta = trim(request.getParameter("numeroCarta"));
        String scadenza    = trim(request.getParameter("scadenza"));
        String cvv         = trim(request.getParameter("cvv"));
        String paypalEmail = trim(request.getParameter("paypalEmail"));

        Map<String,String> errors = validateAll(nome,cognome,indirizzo,cap,citta,provincia,paese,telefono,metodo,
                                                numeroCarta,scadenza,cvv,paypalEmail);

        if (errors.isEmpty()) {
            response.getWriter().write("{\"ok\":true}");
        } else {
            // scegli un campo da mettere a fuoco (il primo errore)
            String focus = errors.keySet().iterator().next();
            StringBuilder sb = new StringBuilder();
            sb.append("{\"ok\":false,\"focus\":\"").append(escapeJson(focus)).append("\",\"errors\":{");
            boolean first = true;
            for (Map.Entry<String,String> e : errors.entrySet()) {
                if (!first) sb.append(",");
                sb.append("\"").append(escapeJson(e.getKey())).append("\":")
                  .append("\"").append(escapeJson(e.getValue())).append("\"");
                first = false;
            }
            sb.append("}}");
            response.getWriter().write(sb.toString());
        }
    }

    /* ===========================================================
       AJAX: validazione campo singolo (live)
       Risposta: {ok:boolean, message?:string}
       =========================================================== */
    private void handleAjaxValidation(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.setContentType("application/json; charset=UTF-8");

        String field = trim(request.getParameter("field"));
        String value = trim(request.getParameter("value"));
        boolean ok;
        String message = null;

        switch (field) {
            case "nome":
            case "cognome":
            case "citta":
            case "paese":
                ok = validNome(value);
                if (!ok) message = "Inserisci almeno 2 lettere (senza numeri).";
                break;
            case "indirizzo":
                ok = validIndirizzo(value);
                if (!ok) message = "Usa almeno 3 caratteri (via e numero civico).";
                break;
            case "cap":
                ok = validCap(value);
                if (!ok) message = "CAP non valido.";
                break;
            case "provincia":
                ok = validProvincia(value);
                if (!ok) message = "Due lettere (es. NA).";
                break;
            case "telefono":
                ok = validTelefono(value);
                if (!ok) message = "Telefono non valido.";
                break;
            case "numeroCarta":
                ok = value != null && P_CC.matcher(value).matches();
                if (!ok) message = "Numero carta non valido (16 cifre).";
                break;
            case "scadenza":
                ok = value != null && P_SCAD.matcher(value).matches();
                if (!ok) message = "Formato MM/AA.";
                break;
            case "cvv":
                ok = value != null && P_CVV.matcher(value).matches();
                if (!ok) message = "CVV di 3 cifre.";
                break;
            case "paypalEmail":
                ok = validEmail(value);
                if (!ok) message = "Email non valida.";
                break;
            default:
                ok = true;
        }

        String json = "{\"ok\":" + ok + (message != null ? ",\"message\":\"" + escapeJson(message) + "\"": "") + "}";
        response.getWriter().write(json);
    }

    /* ===========================================================
       Validazione completa server-side: restituisce mappa errori {fieldId -> messaggio}
       Gli id usati corrispondono agli id degli input in JSP (es. "nome","cap","numeroCarta", …)
       =========================================================== */
    private Map<String,String> validateAll(
            String nome, String cognome, String indirizzo, String cap, String citta, String provincia,
            String paese, String telefono, String metodo,
            String numeroCarta, String scadenza, String cvv, String paypalEmail
    ) {
        Map<String,String> errors = new HashMap<>();

        if (isBlank(nome))      errors.put("nome", "Campo obbligatorio.");
        else if (!validNome(nome)) errors.put("nome", "Inserisci almeno 2 lettere (senza numeri).");

        if (isBlank(cognome))      errors.put("cognome", "Campo obbligatorio.");
        else if (!validNome(cognome)) errors.put("cognome", "Inserisci almeno 2 lettere (senza numeri).");

        if (isBlank(indirizzo))      errors.put("indirizzo", "Campo obbligatorio.");
        else if (!validIndirizzo(indirizzo)) errors.put("indirizzo", "Inserisci un indirizzo valido (via e numero).");

        if (isBlank(cap))      errors.put("cap", "Campo obbligatorio.");
        else if (!validCap(cap)) errors.put("cap", "CAP non valido.");

        if (isBlank(citta))      errors.put("citta", "Campo obbligatorio.");
        else if (!validNome(citta)) errors.put("citta", "Inserisci almeno 2 lettere.");

        if (isBlank(provincia))      errors.put("provincia", "Campo obbligatorio.");
        else if (!validProvincia(provincia)) errors.put("provincia", "Usa due lettere (es. NA).");

        if (isBlank(paese))      errors.put("paese", "Campo obbligatorio.");
        else if (!validNome(paese)) errors.put("paese", "Inserisci almeno 2 lettere.");

        if (isBlank(telefono))      errors.put("telefono", "Campo obbligatorio.");
        else if (!validTelefono(telefono)) errors.put("telefono", "Telefono non valido.");

        if (isBlank(metodo)) {
            errors.put("metodoPagamento", "Seleziona un metodo di pagamento.");
        } else {
            switch (metodo) {
                case "carta":
                    if (isBlank(numeroCarta)) errors.put("numeroCarta", "Campo obbligatorio.");
                    else if (!P_CC.matcher(numeroCarta).matches()) errors.put("numeroCarta", "Numero carta non valido (16 cifre).");
                    if (isBlank(scadenza)) errors.put("scadenza", "Campo obbligatorio.");
                    else if (!P_SCAD.matcher(scadenza).matches()) errors.put("scadenza", "Formato MM/AA.");
                    if (isBlank(cvv)) errors.put("cvv", "Campo obbligatorio.");
                    else if (!P_CVV.matcher(cvv).matches()) errors.put("cvv", "CVV di 3 cifre.");
                    break;
                case "paypal":
                    if (isBlank(paypalEmail)) errors.put("paypalEmail", "Campo obbligatorio.");
                    else if (!validEmail(paypalEmail)) errors.put("paypalEmail", "Email non valida.");
                    break;
                case "bonifico":
                    // nessun extra
                    break;
                default:
                    errors.put("metodoPagamento", "Metodo di pagamento non valido.");
            }
        }

        return errors;
    }

    // ==============================
    // Helpers di validazione
    // ==============================
    private static String trim(String s) { return (s == null) ? null : s.trim(); }
    private static String nonNull(String s) { return (s == null) ? "" : s; }
    private static boolean isBlank(String s){ return s == null || s.trim().isEmpty(); }

    private static boolean validNome(String s) {
        return s != null && P_TXT.matcher(s).matches();
    }
    private static boolean validIndirizzo(String s) {
        return s != null && P_ADDR.matcher(s).matches();
    }
    private static boolean validCap(String s) {
        return s != null && P_CAP.matcher(s).matches();
    }
    private static boolean validProvincia(String s) {
        return s != null && P_PROV.matcher(s).matches();
    }
    private static boolean validTelefono(String s) {
        return s != null && P_TEL.matcher(s).matches();
    }
    private static boolean validEmail(String s) {
        return s != null && P_MAIL.matcher(s).matches();
    }

    private static String escapeJson(String s) {
        if (s == null) return "";
        return s.replace("\\", "\\\\").replace("\"", "\\\"");
    }
}
