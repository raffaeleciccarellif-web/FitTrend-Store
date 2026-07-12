package control;

import dao.ProdottoDAO;
import model.Prodotto;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@WebServlet("/prodotto")
public class DettaglioProdottoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response) throws ServletException, IOException {
        String idParam = request.getParameter("id");
        if (idParam == null || idParam.trim().isEmpty()) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto mancante");
            return;
        }

        int id;
        try {
            id = Integer.parseInt(idParam);
        } catch (NumberFormatException e) {
            response.sendError(HttpServletResponse.SC_BAD_REQUEST, "ID prodotto non valido");
            return;
        }

        try {
            ProdottoDAO prodottoDAO = new ProdottoDAO();
            Prodotto prodotto = prodottoDAO.doRetrieveByKey(id);

            if (prodotto == null || prodotto.isDeleted()) {
                response.sendError(HttpServletResponse.SC_NOT_FOUND, "Prodotto non trovato");
                return;
            }

            request.setAttribute("prodotto", prodotto);
            request.getRequestDispatcher("/WEB-INF/view/dettaglio.jsp").forward(request, response);

        } catch (Exception e) {
            e.printStackTrace();
            response.sendError(HttpServletResponse.SC_INTERNAL_SERVER_ERROR, "Errore interno durante il recupero del prodotto");
        }
    }
}
