package control;

import dao.RimborsoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Rimborso;

import java.io.IOException;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/admin/rimborsi")
public class AdminRimborsiServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {


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

        String stato = request.getParameter("stato");

        try {
            RimborsoDAO dao = new RimborsoDAO();
            
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
            int limit = 15; // rimborsi per pagina in admin
            int offset = (page - 1) * limit;

            int totaleRimborsi = dao.countAll(stato);
            int totalePagine = (int) Math.ceil((double) totaleRimborsi / limit);

            Collection<Rimborso> rimborsi = dao.doRetrieveAll(stato, offset, limit);
            request.setAttribute("rimborsi", rimborsi);
            
            // Attributi per la paginazione
            request.setAttribute("paginaCorrente", page);
            request.setAttribute("totalePagine", totalePagine);
            
            // Costruzione URL base per mantenere i filtri nella paginazione
            StringBuilder baseUrl = new StringBuilder(request.getContextPath()).append("/admin/rimborsi?");
            if (stato != null) baseUrl.append("stato=").append(stato).append("&");
            
            String urlString = baseUrl.toString();
            if (urlString.endsWith("&") || urlString.endsWith("?")) {
                urlString = urlString.substring(0, urlString.length() - 1);
            }
            request.setAttribute("baseUrl", urlString);
            request.getRequestDispatcher("/WEB-INF/view/admin_rimborsi.jsp").forward(request, response);
        } catch (SQLException e) {
            log("Errore:", e);
            request.setAttribute("errore", "Errore nel caricamento dei rimborsi");
            request.getRequestDispatcher("/WEB-INF/view/admin_rimborsi.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        String action = request.getParameter("action");
        if ("aggiornaStato".equals(action)) {
            try {
                int idRimborso = Integer.parseInt(request.getParameter("idRimborso"));
                String nuovoStato = request.getParameter("nuovoStato");

                RimborsoDAO dao = new RimborsoDAO();
                dao.aggiornaStato(idRimborso, nuovoStato);

                request.getSession().setAttribute("messaggio", "Stato rimborso #" + idRimborso + " aggiornato a " + nuovoStato);
            } catch (NumberFormatException e) {
                request.getSession().setAttribute("errore", "ID Rimborso non valido");
            } catch (IllegalStateException e) {
                request.getSession().setAttribute("errore", "Impossibile aggiornare lo stato: " + e.getMessage());
            } catch (Exception e) {
                log("Errore:", e);
                request.getSession().setAttribute("errore", "Errore di sistema durante l'aggiornamento del rimborso");
            }
        }

        response.sendRedirect(request.getContextPath() + "/admin/rimborsi");
    }
}
