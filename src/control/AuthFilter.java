package control;

import jakarta.servlet.*;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import java.io.IOException;

@WebFilter(urlPatterns = {"/admin/*", "/checkout", "/ordini", "/rimborsi"})
public class AuthFilter implements Filter {

    @Override
    public void init(FilterConfig filterConfig) {
        // Initialization if needed
    }

    @Override
    public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
            throws IOException, ServletException {

        HttpServletRequest req = (HttpServletRequest) request;
        HttpServletResponse res = (HttpServletResponse) response;

        String uri = req.getRequestURI();
        String contextPath = req.getContextPath();
        String path = uri.substring(contextPath.length());

        if (path.startsWith("/admin/")) {
            if (!AuthHelper.isAdmin(req)) {
                if (!AuthHelper.isLogged(req)) {
                    AuthHelper.redirectToLogin(req, res);
                } else {
                    AuthHelper.sendForbidden(res);
                }
                return;
            }
        } else if (path.equals("/checkout") || path.equals("/ordini") || path.equals("/rimborsi")) {
            if (!AuthHelper.isLogged(req)) {
                AuthHelper.redirectToLogin(req, res);
                return;
            }
        }

        chain.doFilter(request, response);
    }

    @Override
    public void destroy() {
        // Cleanup if needed
    }
}
