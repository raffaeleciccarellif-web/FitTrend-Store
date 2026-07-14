package control;

import dao.OrdineDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Ordine;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/admin/ordini")
public class AdminOrdiniServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthHelper.isAdmin(request)) {
            AuthHelper.sendForbidden(response);
            return;
        }
        
        HttpSession session = request.getSession();
        String msg = (String) session.getAttribute("messaggio");
        if (msg != null) {
            request.setAttribute("messaggio", msg);
            session.removeAttribute("messaggio");
        }
        String err = (String) session.getAttribute("errore");
        if (err != null) {
            request.setAttribute("errore", err);
            session.removeAttribute("errore");
        }

        String dataInizio = request.getParameter("dataInizio");
        String dataFine = request.getParameter("dataFine");
        String utenteIdStr = request.getParameter("utenteId");
        String stato = request.getParameter("stato");

        Integer utenteId = null;
        if (utenteIdStr != null && !utenteIdStr.trim().isEmpty()) {
            try {
                utenteId = Integer.parseInt(utenteIdStr);
            } catch (NumberFormatException ignored) {}
        }

        try {
            OrdineDAO dao = new OrdineDAO();
            Collection<Ordine> ordini = dao.doRetrieveByFilters(dataInizio, dataFine, utenteId, stato);
            request.setAttribute("ordini", ordini);
            request.getRequestDispatcher("/WEB-INF/view/admin_ordini.jsp").forward(request, response);
        } catch (SQLException e) {
            e.printStackTrace();
            request.setAttribute("errore", "Errore nel caricamento degli ordini");
            request.getRequestDispatcher("/WEB-INF/view/admin_ordini.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        if (!AuthHelper.isAdmin(request)) {
            AuthHelper.sendForbidden(response);
            return;
        }

        String action = request.getParameter("action");
        if ("aggiornaStato".equals(action)) {
            try {
                int ordineId = Integer.parseInt(request.getParameter("ordineId"));
                String nuovoStato = request.getParameter("nuovoStato");
                
                OrdineDAO dao = new OrdineDAO();
                dao.doUpdateStato(ordineId, nuovoStato);
                
                request.getSession().setAttribute("messaggio", "Stato ordine #" + ordineId + " aggiornato a " + nuovoStato);
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errore", "ID Ordine non valido");
            } catch (IllegalStateException e) {
                request.getSession().setAttribute("errore", "Impossibile aggiornare lo stato: " + e.getMessage());
            } catch (Exception e) {
                e.printStackTrace();
                request.getSession().setAttribute("errore", "Errore di sistema durante l'aggiornamento dello stato");
            }
        }
        
        response.sendRedirect(request.getContextPath() + "/admin/ordini");
    }
}
