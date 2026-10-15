package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.DashboardService;

/** Panel de inicio: indicadores para el administrador; el empleado va a "Mis activos". */
@WebServlet(urlPatterns = {"", "/inicio"})
public class InicioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient DashboardService dashboard = new DashboardService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        if (!u.isAdministrador()) {
            resp.sendRedirect(req.getContextPath() + "/mis-activos");
            return;
        }
        Mensajes.mostrar(req);
        req.setAttribute("resumen", dashboard.resumen());
        req.setAttribute("estados", EstadoActivo.values());
        req.getRequestDispatcher("/WEB-INF/views/inicio.jsp").forward(req, resp);
    }
}
