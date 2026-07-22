package control;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import model.Utente;

public class AuthHelper {

    public static boolean isLogged(HttpServletRequest request) {
        HttpSession session = request.getSession(false);
        if (session != null) {
            String userToken = (String) session.getAttribute("userToken");
            Utente utenteLoggato = (Utente) session.getAttribute("utenteLoggato");
            return userToken != null && utenteLoggato != null;
        }
        return false;
    }

    public static boolean isAdmin(HttpServletRequest request) {
        if (isLogged(request)) {
            HttpSession session = request.getSession(false);
            Utente utenteLoggato = (Utente) session.getAttribute("utenteLoggato");
            return utenteLoggato != null && utenteLoggato.isAdmin();
        }
        return false;
    }

    public static void redirectToLogin(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/login");
    }

    public static void redirectToHome(HttpServletRequest request, HttpServletResponse response) throws IOException {
        response.sendRedirect(request.getContextPath() + "/home");
    }
}
