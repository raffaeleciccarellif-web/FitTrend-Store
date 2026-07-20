package control;

import dao.OrdineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Carrello;
import model.Ordine;
import model.Utente;

import java.io.IOException;
import java.sql.SQLException;

@WebServlet("/checkout")
public class CheckoutServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    private final OrdineDAO ordineDAO = new OrdineDAO();

    // GET: verifica login e carrello non vuoto, poi forward a checkout.jsp
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        HttpSession session = request.getSession(false);



        // Controllo carrello non vuoto
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello?action=visualizza");
            return;
        }

        request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
    }

    // POST: valida i dati del form, crea l'ordine e svuota il carrello solo dopo commit
    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        request.setCharacterEncoding("UTF-8");
        HttpSession session = request.getSession(false);



        // Controllo carrello non vuoto
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null || carrello.isEmpty()) {
            response.sendRedirect(request.getContextPath() + "/carrello?action=visualizza");
            return;
        }

        Utente utente = (Utente) session.getAttribute("utenteLoggato");

        // Lettura parametri dal form
        String indirizzo       = trim(request.getParameter("indirizzoSpedizione"));
        String citta           = trim(request.getParameter("cittaSpedizione"));
        String cap             = trim(request.getParameter("capSpedizione"));
        String metodoPagamento = trim(request.getParameter("metodoPagamento"));
        String numeroCarta     = trim(request.getParameter("numeroCarta")); // solo per validazione locale, non salvato

        // Validazione server-side
        String errore = valida(indirizzo, citta, cap, metodoPagamento, numeroCarta);
        if (errore != null) {
            request.setAttribute("errore", errore);
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
            return;
        }

        // Costruzione bean Ordine: numero carta completo mai salvato
        Ordine ordine = new Ordine();
        ordine.setUtenteId(utente.getId());
        ordine.setIndirizzoSpedizione(indirizzo);
        ordine.setCittaSpedizione(citta);
        ordine.setCapSpedizione(cap);
        ordine.setMetodoPagamento(metodoPagamento);

        if ("carta".equals(metodoPagamento) && numeroCarta != null && numeroCarta.length() >= 4) {
            // Salva solo le ultime 4 cifre, scarta il numero completo
            ordine.setUltimeCifreCarta(numeroCarta.substring(numeroCarta.length() - 4));
        } else {
            ordine.setUltimeCifreCarta(null);
        }

        // Salvataggio ordine transazionale
        try {
            int idOrdine = ordineDAO.salvaOrdine(ordine, carrello);

            // Svuota il carrello SOLO dopo il commit riuscito
            carrello.svuota();

            // Flash attribute per la conferma
            session.setAttribute("messaggioOrdine", "Ordine #" + idOrdine + " effettuato con successo!");
            response.sendRedirect(request.getContextPath() + "/ordini");

        } catch (SQLException e) {
            // Errore di stock o DB: messaggio leggibile all'utente, niente stack trace esposto
            String msgUtente = e.getMessage();
            if (msgUtente == null || msgUtente.isBlank()) {
                msgUtente = "Errore durante il salvataggio dell'ordine. Riprova.";
            }
            request.setAttribute("errore", msgUtente);
            request.getRequestDispatcher("/WEB-INF/view/checkout.jsp").forward(request, response);
        }
    }

    // Validazione server-side di tutti i campi obbligatori
    // Ritorna il messaggio di errore o null se tutto è valido
    private String valida(String indirizzo, String citta, String cap,
                          String metodoPagamento, String numeroCarta) {

        if (indirizzo == null || indirizzo.isBlank())
            return "L'indirizzo di spedizione è obbligatorio.";
        if (indirizzo.length() > 255)
            return "L'indirizzo non può superare i 255 caratteri.";

        if (citta == null || citta.isBlank())
            return "La città è obbligatoria.";
        if (citta.length() > 80)
            return "La città non può superare gli 80 caratteri.";

        if (cap == null || cap.isBlank())
            return "Il CAP è obbligatorio.";
        if (!cap.matches("\\d{5}"))
            return "Il CAP deve essere composto da 5 cifre numeriche.";

        // Whitelist metodi di pagamento accettati
        if (metodoPagamento == null || (!metodoPagamento.equals("carta") && !metodoPagamento.equals("paypal") && !metodoPagamento.equals("contrassegno")))
            return "Metodo di pagamento non valido.";

        // Validazione carta solo se metodo = carta
        if ("carta".equals(metodoPagamento)) {
            if (numeroCarta == null || numeroCarta.isBlank())
                return "Il numero di carta è obbligatorio per il pagamento con carta.";
            // Rimuove spazi prima di validare
            String cartaPulita = numeroCarta.replaceAll("\\s+", "");
            if (!cartaPulita.matches("\\d{13,19}"))
                return "Il numero di carta non è valido (deve contenere tra 13 e 19 cifre).";
        }

        return null; // nessun errore
    }

    // Helper: trim sicuro che gestisce null
    private String trim(String s) {
        return s != null ? s.trim() : null;
    }
}
