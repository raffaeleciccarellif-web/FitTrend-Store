package control;

import dao.UtenteDAO;
import model.Utente;

import java.io.IOException;
import java.io.Serial;
import java.util.UUID;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        if (email == null || email.trim().isEmpty() || password == null || password.trim().isEmpty()) {
            request.setAttribute("errore", "Credenziali non valide");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            return;
        }

        try {
            UtenteDAO utenteDAO = new UtenteDAO();
            Utente utente = utenteDAO.doRetrieveByEmailAndPassword(email, password);

            if (utente != null) {
                HttpSession vecchiSessione = request.getSession(false);
                if (vecchiSessione != null) {
                    vecchiSessione.invalidate();
                }
                
                HttpSession nuovaSessione = request.getSession(true);
                String userToken = UUID.randomUUID().toString();
                
                nuovaSessione.setAttribute("userToken", userToken);
                nuovaSessione.setAttribute("utenteLoggato", utente);
                
                response.sendRedirect(request.getContextPath() + "/catalogo");
            } else {
                request.setAttribute("errore", "Credenziali non valide");
                request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
            }
        } catch (Exception e) {
            log("Errore:", e);
            request.setAttribute("errore", "Errore interno durante il login");
            request.getRequestDispatcher("/WEB-INF/view/login.jsp").forward(request, response);
        }
    }
}
