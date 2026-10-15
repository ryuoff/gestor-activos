package pe.edu.utp.gestoractivos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.Usuario;

/** Regla de negocio: solo el administrador gestiona activos, asignaciones, empleados y usuarios. */
public class AdminFilter implements Filter {

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        Usuario u = (Usuario) request.getSession().getAttribute("usuario");
        if (u != null && u.isAdministrador()) {
            chain.doFilter(req, res);
        } else {
            ((HttpServletResponse) res).sendError(HttpServletResponse.SC_FORBIDDEN,
                    "Solo el administrador puede acceder a esta opción.");
        }
    }
}
