package pe.edu.utp.gestoractivos.filter;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

/**
 * Protección CSRF (token por sesión). Todo formulario POST debe enviar el campo oculto _csrf
 * con el valor de la sesión; si no coincide se responde 403.
 */
public class CsrfFilter implements Filter {
    public static final String ATRIBUTO = "csrf";
    public static final String CAMPO = "_csrf";

    @Override
    public void doFilter(ServletRequest req, ServletResponse res, FilterChain chain)
            throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) req;
        HttpServletResponse response = (HttpServletResponse) res;
        if (EstaticosUtil.esEstatico(request)) {
            chain.doFilter(req, res);
            return;
        }
        HttpSession session = request.getSession(true);
        String esperado = (String) session.getAttribute(ATRIBUTO);
        if (esperado == null) {
            esperado = PasswordUtil.tokenAleatorio(24);
            session.setAttribute(ATRIBUTO, esperado);
        }
        if ("POST".equalsIgnoreCase(request.getMethod())) {
            String recibido = request.getParameter(CAMPO);
            if (recibido == null || !MessageDigest.isEqual(
                    esperado.getBytes(StandardCharsets.UTF_8), recibido.getBytes(StandardCharsets.UTF_8))) {
                response.sendError(HttpServletResponse.SC_FORBIDDEN,
                        "La solicitud no es válida o la sesión expiró. Vuelve a cargar la página e inténtalo de nuevo.");
                return;
            }
        }
        chain.doFilter(req, res);
    }
}
