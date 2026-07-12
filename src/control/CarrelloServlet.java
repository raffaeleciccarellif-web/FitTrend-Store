package control;

import dao.ProdottoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Carrello;
import model.ItemCarrello;
import model.Prodotto;

import java.io.IOException;

@WebServlet("/carrello")
public class CarrelloServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO = new ProdottoDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        processRequest(request, response);
    }

    private void processRequest(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        HttpSession session = request.getSession();
        Carrello carrello = (Carrello) session.getAttribute("carrello");
        if (carrello == null) {
            carrello = new Carrello();
            session.setAttribute("carrello", carrello);
        }

        String action = request.getParameter("action");
        if (action == null) {
            action = "visualizza";
        }

        boolean isAjax = "XMLHttpRequest".equals(request.getHeader("X-Requested-With"));
        boolean success = true;
        String message = "";

        try {
            switch (action) {
                case "aggiungi":
                    int idAgg = Integer.parseInt(request.getParameter("id"));
                    int qtaAgg = 1;
                    if (request.getParameter("quantita") != null) {
                        qtaAgg = Integer.parseInt(request.getParameter("quantita"));
                    }
                    if (qtaAgg <= 0) {
                        success = false;
                        message = "Quantità non valida.";
                        break;
                    }

                    Prodotto pAgg = prodottoDAO.doRetrieveByKey(idAgg);
                    if (pAgg == null) {
                        success = false;
                        message = "Prodotto inesistente, esaurito o rimosso dal catalogo.";
                        break;
                    }

                    // Calcolo della quantità totale se l'utente ha già questo prodotto nel carrello
                    int qtaPresente = 0;
                    for (ItemCarrello item : carrello.getItems()) {
                        if (item.getIdProdotto() == idAgg) {
                            qtaPresente = item.getQuantita();
                            break;
                        }
                    }

                    if (qtaPresente + qtaAgg > pAgg.getQuantitaDisponibile()) {
                        success = false;
                        message = "Stock insufficiente. Disponibili: " + pAgg.getQuantitaDisponibile() + ", nel carrello: " + qtaPresente;
                    } else {
                        // Mai fidarsi del prezzo/immagine del client, prendiamo tutto dal DB
                        ItemCarrello nuovoItem = new ItemCarrello(pAgg.getId(), pAgg.getNome(), pAgg.getPrezzo(), pAgg.getImmagine(), qtaAgg);
                        carrello.add(nuovoItem);
                        message = "Prodotto aggiunto al carrello.";
                    }
                    break;

                case "modifica":
                    int idMod = Integer.parseInt(request.getParameter("id"));
                    int qtaMod = Integer.parseInt(request.getParameter("quantita"));

                    if (qtaMod <= 0) {
                        carrello.remove(idMod);
                        message = "Prodotto rimosso dal carrello.";
                    } else {
                        Prodotto pMod = prodottoDAO.doRetrieveByKey(idMod);
                        if (pMod == null) {
                            carrello.remove(idMod);
                            success = false;
                            message = "Il prodotto non è più disponibile ed è stato rimosso dal carrello.";
                        } else if (qtaMod > pMod.getQuantitaDisponibile()) {
                            success = false;
                            message = "Stock insufficiente. Disponibilità massima: " + pMod.getQuantitaDisponibile();
                        } else {
                            carrello.update(idMod, qtaMod);
                            message = "Quantità aggiornata.";
                        }
                    }
                    break;

                case "rimuovi":
                    int idRim = Integer.parseInt(request.getParameter("id"));
                    carrello.remove(idRim);
                    message = "Prodotto rimosso dal carrello.";
                    break;

                case "svuota":
                    carrello.svuota();
                    message = "Carrello svuotato.";
                    break;

                case "visualizza":
                    request.getRequestDispatcher("/WEB-INF/view/carrello.jsp").forward(request, response);
                    return; // Terminare l'esecuzione per evitare redirect successivi
            }
        } catch (NumberFormatException e) {
            success = false;
            message = "Formato dati non valido.";
        } catch (Exception e) {
            success = false;
            message = "Errore interno del server.";
            e.printStackTrace();
        }

        // Risposta AJAX
        if (isAjax) {
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");
            
            // Creazione stringa JSON senza librerie esterne
            String jsonResponse = String.format(
                    "{\"success\": %b, \"itemCount\": %d, \"total\": \"%s\", \"message\": \"%s\"}",
                    success,
                    carrello.getNumeroTotaleArticoli(),
                    carrello.getTotaleProvvisorio().toString(),
                    escapeJson(message)
            );
            
            response.getWriter().write(jsonResponse);
        } else {
            // Risposta per chiamate standard (non-AJAX)
            if (!success && !message.isEmpty()) {
                request.getSession().setAttribute("erroreCarrello", message);
            }
            response.sendRedirect(request.getContextPath() + "/carrello?action=visualizza");
        }
    }

    private String escapeJson(String input) {
        if (input == null) return "";
        return input.replace("\\", "\\\\")
                    .replace("\"", "\\\"")
                    .replace("\n", "\\n")
                    .replace("\r", "\\r");
    }
}
