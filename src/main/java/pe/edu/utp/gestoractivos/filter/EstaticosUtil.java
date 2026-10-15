package pe.edu.utp.gestoractivos.filter;

import jakarta.servlet.http.HttpServletRequest;

/** Detecta recursos estáticos (hojas de estilo, imágenes, scripts) que no requieren sesión. */
final class EstaticosUtil {
    private EstaticosUtil() { }

    static String ruta(HttpServletRequest request) {
        return request.getRequestURI().substring(request.getContextPath().length());
    }

    static boolean esEstatico(HttpServletRequest request) {
        String p = ruta(request).toLowerCase();
        return p.startsWith("/css/") || p.startsWith("/img/") || p.startsWith("/js/")
                || p.endsWith(".css") || p.endsWith(".js") || p.endsWith(".png")
                || p.endsWith(".svg") || p.endsWith(".ico") || p.endsWith(".jpg");
    }
}
