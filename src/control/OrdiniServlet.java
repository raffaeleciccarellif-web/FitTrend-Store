package control;

import dao.DettaglioOrdineDAO;
import dao.OrdineDAO;
import dao.RimborsoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Ordine;
import model.Rimborso;
import model.Utente;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

@WebServlet("/ordini")
public class OrdiniServlet extends HttpServlet {
    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


        Utente utente = (Utente) request.getSession().getAttribute("utenteLoggato");
        
        // Gestione messaggi flash dalla sessione
        String msg = (String) request.getSession().getAttribute("messaggio");
        if (msg != null) {
            request.setAttribute("messaggio", msg);
            request.getSession().removeAttribute("messaggio");
        }
        String err = (String) request.getSession().getAttribute("errore");
        if (err != null) {
            request.setAttribute("errore", err);
            request.getSession().removeAttribute("errore");
        }
        
        try {
            OrdineDAO ordineDAO = new OrdineDAO();
            DettaglioOrdineDAO dettaglioDAO = new DettaglioOrdineDAO();
            RimborsoDAO rimborsoDAO = new RimborsoDAO();
            
            // Paginazione
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isBlank()) {
                try {
                    page = Integer.parseInt(pageParam.trim());
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {
                    // ignora e usa 1
                }
            }
            int limit = 5; // ordini per pagina
            int offset = (page - 1) * limit;

            int totaleOrdini = ordineDAO.countByUtente(utente.getId());
            int totalePagine = (int) Math.ceil((double) totaleOrdini / limit);

            // Recupera ordini dell'utente
            Collection<Ordine> ordini = ordineDAO.doRetrieveByUtente(utente.getId(), offset, limit);
            
            // Recupera i dettagli storici per ciascun ordine
            for (Ordine o : ordini) {
                o.setDettagli(new java.util.ArrayList<>(dettaglioDAO.doRetrieveByOrdine(o.getId())));
            }

            // Recupera i rimborsi dell'utente e li mappa per ordine_id (carica tutti per la mappa)
            Collection<Rimborso> rimborsiList = rimborsoDAO.doRetrieveByUtente(utente.getId());
            Map<Integer, Rimborso> rimborsiMap = new HashMap<>();
            for (Rimborso r : rimborsiList) {
                rimborsiMap.put(r.getOrdineId(), r);
            }

            request.setAttribute("ordini", ordini);
            request.setAttribute("rimborsi", rimborsiMap);
            
            // Attributi per la paginazione
            request.setAttribute("paginaCorrente", page);
            request.setAttribute("totalePagine", totalePagine);
            request.setAttribute("baseUrl", request.getContextPath() + "/ordini");
            
            request.getRequestDispatcher("/WEB-INF/view/ordini.jsp").forward(request, response);
            
        } catch (SQLException e) {
            log("Errore:", e);
            request.setAttribute("errore", "Errore nel caricamento dello storico ordini.");
            request.getRequestDispatcher("/WEB-INF/view/ordini.jsp").forward(request, response);
        }
    }
}
