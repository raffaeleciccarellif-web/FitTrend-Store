package control;

import dao.CategoriaDAO;
import dao.ProdottoDAO;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Categoria;
import model.Prodotto;

import java.io.IOException;
import java.math.BigDecimal;
import java.sql.SQLException;
import java.util.Collection;

@WebServlet("/admin/prodotti")
public class AdminProdottiServlet extends HttpServlet {

    private final ProdottoDAO prodottoDAO = new ProdottoDAO();
    private final CategoriaDAO categoriaDAO = new CategoriaDAO();

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {



        String action = request.getParameter("action");
        if (action == null) {
            action = "visualizza";
        }

        try {
            // Paginazione
            int page = 1;
            String pageParam = request.getParameter("page");
            if (pageParam != null && !pageParam.isBlank()) {
                try {
                    page = Integer.parseInt(pageParam.trim());
                    if (page < 1) page = 1;
                } catch (NumberFormatException e) {}
            }
            int limit = 10;
            int offset = (page - 1) * limit;

            int totaleProdotti = prodottoDAO.countAllForAdmin();
            int totalePagine = (int) Math.ceil((double) totaleProdotti / limit);

            if ("visualizza".equals(action)) {
                // Carica categorie e prodotti per la vista
                Collection<Categoria> categorie = categoriaDAO.doRetrieveAll();
                Collection<Prodotto> prodotti = prodottoDAO.doRetrieveAllForAdmin(offset, limit);
                
                request.setAttribute("categorie", categorie);
                request.setAttribute("prodotti", prodotti);
                
                // Attributi per la paginazione
                request.setAttribute("paginaCorrente", page);
                request.setAttribute("totalePagine", totalePagine);
                request.setAttribute("baseUrl", request.getContextPath() + "/admin/prodotti?action=visualizza");

                request.getRequestDispatcher("/WEB-INF/view/admin_catalogo.jsp").forward(request, response);
            } else if ("modifica".equals(action)) {
                // Pre-popola il form
                int id = Integer.parseInt(request.getParameter("id"));
                Prodotto p = prodottoDAO.doRetrieveByKey(id);
                if (p != null) {
                    request.setAttribute("prodottoInModifica", p);
                }
                
                // Ricarica la vista
                Collection<Categoria> categorie = categoriaDAO.doRetrieveAll();
                Collection<Prodotto> prodotti = prodottoDAO.doRetrieveAllForAdmin(offset, limit);
                request.setAttribute("categorie", categorie);
                request.setAttribute("prodotti", prodotti);
                
                // Attributi per la paginazione
                request.setAttribute("paginaCorrente", page);
                request.setAttribute("totalePagine", totalePagine);
                request.setAttribute("baseUrl", request.getContextPath() + "/admin/prodotti?action=modifica&id=" + id);

                request.getRequestDispatcher("/WEB-INF/view/admin_catalogo.jsp").forward(request, response);
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti?error=IdNonValido");
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {



        String action = request.getParameter("action");
        
        try {
            if ("salva".equals(action)) {
                // Legge i parametri
                String idParam = request.getParameter("id");
                String nome = request.getParameter("nome");
                String descrizione = request.getParameter("descrizione");
                String prezzoStr = request.getParameter("prezzo");
                String categoriaIdStr = request.getParameter("categoriaId");
                String immagine = request.getParameter("immagine");
                String quantitaStr = request.getParameter("quantita");

                // Validazione base
                if (nome == null || nome.isBlank() || categoriaIdStr == null || categoriaIdStr.isBlank() || immagine == null || immagine.isBlank()) {
                    response.sendRedirect(request.getContextPath() + "/admin/prodotti?error=CampiObbligatori");
                    return;
                }

                int categoriaId = Integer.parseInt(categoriaIdStr);
                BigDecimal prezzo = new BigDecimal(prezzoStr);
                int quantita = Integer.parseInt(quantitaStr);

                if (prezzo.compareTo(BigDecimal.ZERO) <= 0 || quantita < 0) {
                    response.sendRedirect(request.getContextPath() + "/admin/prodotti?error=ValoriNumericiNonValidi");
                    return;
                }

                // Verifica l'esistenza della Categoria
                Categoria categoria = categoriaDAO.doRetrieveByKey(categoriaId);
                if (categoria == null) {
                    response.sendRedirect(request.getContextPath() + "/admin/prodotti?error=CategoriaInesistente");
                    return;
                }

                Prodotto p = new Prodotto();
                p.setNome(nome);
                p.setDescrizione(descrizione);
                p.setPrezzo(prezzo);
                p.setCategoriaId(categoria.getId());
                p.setImmagine(immagine);
                p.setQuantitaDisponibile(quantita);
                p.setDeleted(false);

                if (idParam == null || idParam.isBlank()) {
                    // Create
                    prodottoDAO.doSave(p);
                } else {
                    // Update
                    p.setId(Integer.parseInt(idParam));
                    prodottoDAO.doUpdate(p);
                }

                response.sendRedirect(request.getContextPath() + "/admin/prodotti?msg=ProdottoSalvato");
                
            } else if ("elimina".equals(action)) {
                // Delete logico
                int id = Integer.parseInt(request.getParameter("id"));
                prodottoDAO.doDelete(id);
                response.sendRedirect(request.getContextPath() + "/admin/prodotti?msg=ProdottoEliminato");
            }
        } catch (SQLException e) {
            throw new ServletException(e);
        } catch (NumberFormatException e) {
            response.sendRedirect(request.getContextPath() + "/admin/prodotti?error=FormatoDatiNonValido");
        }
    }
}
