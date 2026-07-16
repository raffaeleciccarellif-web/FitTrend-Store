package control;

import dao.UtenteDAO;
import model.Utente;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.io.Serial;
import java.sql.SQLException;
import java.util.regex.Pattern;

@WebServlet("/registrazione")
public class RegistrazioneServlet extends HttpServlet {
    @Serial
    private static final long serialVersionUID = 1L;
    private final UtenteDAO utenteDAO = new UtenteDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        request.getRequestDispatcher("/WEB-INF/view/registrazione.jsp").forward(request, response);
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        String nome = request.getParameter("nome");
        String cognome = request.getParameter("cognome");
        String email = request.getParameter("email");
        String password = request.getParameter("password");

        // Validazione lato server
        if (nome == null || nome.trim().isEmpty() ||
            cognome == null || cognome.trim().isEmpty()) {
            request.setAttribute("errore", "Nome e cognome non possono essere vuoti.");
            doGet(request, response);
            return;
        }

        // RegEx validazione email
        String emailRegex = "^[A-Za-z0-9+_.-]+@(.+)$";
        if (email == null || !Pattern.matches(emailRegex, email)) {
            request.setAttribute("errore", "Inserisci un indirizzo email valido.");
            doGet(request, response);
            return;
        }

        // Password: almeno 8 caratteri, una maiuscola e un numero
        String passwordRegex = "^(?=.*[A-Z])(?=.*\\d).{8,}$";
        if (password == null || !Pattern.matches(passwordRegex, password)) {
            request.setAttribute("errore", "La password deve contenere almeno 8 caratteri, una lettera maiuscola e un numero.");
            doGet(request, response);
            return;
        }

        try {
            // Controllo email duplicata
            if (utenteDAO.doRetrieveByEmail(email) != null) {
                request.setAttribute("errore", "Questa email è già registrata.");
                doGet(request, response);
                return;
            }

            // Creazione e salvataggio dell'utente
            Utente utente = new Utente();
            utente.setNome(nome.trim());
            utente.setCognome(cognome.trim());
            utente.setEmail(email.trim());
            utente.setAdmin(false);

            utenteDAO.doSave(utente, password);

            // Successo: redirect alla pagina di login
            response.sendRedirect(request.getContextPath() + "/login");

        } catch (SQLException e) {
            log("Errore SQL durante la registrazione", e);
            request.setAttribute("errore", "Si è verificato un errore interno. Riprova più tardi.");
            doGet(request, response);
        }
    }
}
