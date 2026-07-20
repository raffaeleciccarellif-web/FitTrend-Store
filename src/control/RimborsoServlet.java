package control;

import dao.RimborsoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.Rimborso;
import model.Utente;

import java.io.IOException;
import java.util.Collection;

@WebServlet("/rimborsi")
public class RimborsoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        Utente utente = (Utente) request.getSession().getAttribute("utenteLoggato");

        try {
            RimborsoDAO rimborsoDAO = new RimborsoDAO();
            Collection<Rimborso> rimborsi = rimborsoDAO.doRetrieveByUtente(utente.getId());
            request.setAttribute("rimborsi", rimborsi);
        } catch (Exception e) {
            log("Errore nel caricamento dei rimborsi utente:", e);
            request.setAttribute("errore", "Si è verificato un errore nel caricamento dei rimborsi.");
        }

        HttpSession session = request.getSession();
        if (session.getAttribute("messaggio") != null) {
            request.setAttribute("messaggio", session.getAttribute("messaggio"));
            session.removeAttribute("messaggio");
        }
        if (session.getAttribute("errore") != null) {
            request.setAttribute("errore", session.getAttribute("errore"));
            session.removeAttribute("errore");
        }

        request.getRequestDispatcher("/WEB-INF/view/rimborsi.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {


        Utente utente = (Utente) request.getSession().getAttribute("utenteLoggato");
        HttpSession session = request.getSession();
        
        String action = request.getParameter("action");
        
        if ("richiedi".equals(action)) {
            try {
                int ordineId = Integer.parseInt(request.getParameter("ordineId"));
                String motivo = request.getParameter("motivo");

                if (motivo == null || motivo.trim().isEmpty()) {
                    session.setAttribute("errore", "Il motivo del rimborso è obbligatorio.");
                    response.sendRedirect(request.getContextPath() + "/ordini");
                    return;
                }

                RimborsoDAO rimborsoDAO = new RimborsoDAO();
                // Il DAO effettua i controlli: proprietà, stato, assenza di rimborso precedente, ricalcolo importo.
                rimborsoDAO.richiediRimborso(ordineId, utente.getId(), motivo.trim());
                
                session.setAttribute("messaggio", "Richiesta di rimborso inoltrata con successo.");
                response.sendRedirect(request.getContextPath() + "/ordini");
                
            } catch (NumberFormatException e) {
                session.setAttribute("errore", "Formato ID ordine non valido.");
                response.sendRedirect(request.getContextPath() + "/ordini");
            } catch (IllegalStateException e) {
                // Errore controllato: ordine non appartiene all'utente, stato non valido, o rimborso esistente
                session.setAttribute("errore", "Impossibile richiedere il rimborso: " + e.getMessage());
                response.sendRedirect(request.getContextPath() + "/ordini");
            } catch (Exception e) {
                log("Errore:", e);
                session.setAttribute("errore", "Si è verificato un errore durante la richiesta di rimborso.");
                response.sendRedirect(request.getContextPath() + "/ordini");
            }
        } else {
            response.sendRedirect(request.getContextPath() + "/ordini");
        }
    }
}
