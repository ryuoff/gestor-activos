package pe.edu.utp.gestoractivos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Exige sesión iniciada para todo salvo el login y los recursos estáticos. */
public class AuthFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (EstaticosUtil.esEstatico(request)) {
            chain.doFilter(req, res);
            return;
        }
        boolean login = EstaticosUtil.ruta(request).equals("/login");
        boolean logueado = request.getSession(false) != null
                && request.getSession(false).getAttribute("usuario") != null;
        // Las páginas con datos no deben quedar guardadas en la caché del navegador.
        response.setHeader("Cache-Control", "no-store");
        if (login || logueado) {
            chain.doFilter(req, res);
        } else {
            response.sendRedirect(request.getContextPath() + "/login");
        }
    }
}
