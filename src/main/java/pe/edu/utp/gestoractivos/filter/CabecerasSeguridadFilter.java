package pe.edu.utp.gestoractivos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;

/** Agrega cabeceras HTTP de seguridad a todas las respuestas. */
public class CabecerasSeguridadFilter implements Filter {
    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletResponse r = (HttpServletResponse) res;
        r.setHeader("X-Content-Type-Options", "nosniff");
        r.setHeader("X-Frame-Options", "DENY");
        r.setHeader("Referrer-Policy", "same-origin");
        chain.doFilter(req, res);
    }
}
