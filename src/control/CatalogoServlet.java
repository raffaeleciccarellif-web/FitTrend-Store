package control;

import dao.CategoriaDAO;
import dao.ProdottoDAO;
import model.Categoria;
import model.Prodotto;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/catalogo")
public class CatalogoServlet extends HttpServlet {

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {

        // ── Lettura parametri ──────────────────────────────────────────────────
        String nome        = request.getParameter("nome");
        String catIdParam  = request.getParameter("categoriaId");
        String prezzoMinP  = request.getParameter("prezzoMin");
        String prezzoMaxP  = request.getParameter("prezzoMax");
        String order       = request.getParameter("order");

        // ── Validazione categoriaId ────────────────────────────────────────────
        Integer categoriaId = null;
        if (catIdParam != null && !catIdParam.isBlank()) {
            try {
                int parsed = Integer.parseInt(catIdParam.trim());
                if (parsed > 0) {
                    categoriaId = parsed;
                }
            } catch (NumberFormatException e) {
                // valore non numerico: ignora il filtro
            }
        }

        // ── Validazione prezzoMin / prezzoMax ──────────────────────────────────
        BigDecimal prezzoMin = null;
        if (prezzoMinP != null && !prezzoMinP.isBlank()) {
            try {
                BigDecimal val = new BigDecimal(prezzoMinP.trim());
                if (val.compareTo(BigDecimal.ZERO) >= 0) prezzoMin = val;
            } catch (NumberFormatException e) {
                // valore non valido: ignora il filtro
            }
        }

        BigDecimal prezzoMax = null;
        if (prezzoMaxP != null && !prezzoMaxP.isBlank()) {
            try {
                BigDecimal val = new BigDecimal(prezzoMaxP.trim());
                if (val.compareTo(BigDecimal.ZERO) >= 0) prezzoMax = val;
            } catch (NumberFormatException e) {
                // valore non valido: ignora il filtro
            }
        }

        // ── Normalizza nome vuoto ──────────────────────────────────────────────
        if (nome != null && nome.isBlank()) nome = null;

        // ── Caricamento dati da DAO ────────────────────────────────────────────
        try {
            CategoriaDAO categoriaDAO = new CategoriaDAO();
            ProdottoDAO  prodottoDAO  = new ProdottoDAO();

            Collection<Categoria> categorie = categoriaDAO.doRetrieveAll();

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
            int limit = 12; // prodotti per pagina nel catalogo
            int offset = (page - 1) * limit;

            int totaleProdotti = prodottoDAO.countByFilters(nome, categoriaId, prezzoMin, prezzoMax);
            int totalePagine = (int) Math.ceil((double) totaleProdotti / limit);

            Collection<Prodotto> prodotti = prodottoDAO.doRetrieveByFilters(
                    nome, categoriaId, prezzoMin, prezzoMax, order, offset, limit);

            // ── Popolamento attributi request ──────────────────────────────────
            request.setAttribute("categorie",   categorie);
            request.setAttribute("prodotti",     prodotti);

            // Attributi per la paginazione
            request.setAttribute("paginaCorrente", page);
            request.setAttribute("totalePagine", totalePagine);
            
            // Costruzione URL base per mantenere i filtri nella paginazione
            StringBuilder baseUrl = new StringBuilder(request.getContextPath()).append("/catalogo?");
            if (nome != null) baseUrl.append("nome=").append(nome).append("&");
            if (categoriaId != null) baseUrl.append("categoriaId=").append(categoriaId).append("&");
            if (prezzoMin != null) baseUrl.append("prezzoMin=").append(prezzoMin).append("&");
            if (prezzoMax != null) baseUrl.append("prezzoMax=").append(prezzoMax).append("&");
            if (order != null) baseUrl.append("order=").append(order).append("&");
            // Rimuovi ultimo & se presente, e se la query string è vuota rimuovi anche il ?
            String urlString = baseUrl.toString();
            if (urlString.endsWith("&") || urlString.endsWith("?")) {
                urlString = urlString.substring(0, urlString.length() - 1);
            }
            request.setAttribute("baseUrl", urlString);

            // Ripopola i filtri per mantenere i valori nel form
            request.setAttribute("filtroNome",        nome        != null ? nome       : "");
            request.setAttribute("filtroCategoriaId", categoriaId != null ? categoriaId : "");
            request.setAttribute("filtroPrezzoMin",   prezzoMin   != null ? prezzoMin  : "");
            request.setAttribute("filtroPrezzoMax",   prezzoMax   != null ? prezzoMax  : "");
            request.setAttribute("filtroOrder",       order       != null ? order      : "nome");

        } catch (SQLException e) {
            throw new ServletException("Errore durante il caricamento del catalogo", e);
        }

        request.getRequestDispatcher("/WEB-INF/view/catalogo.jsp")
               .forward(request, response);
    }
}
