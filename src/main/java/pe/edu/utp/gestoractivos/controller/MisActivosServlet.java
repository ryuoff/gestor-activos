package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.AsignacionService;

/** Consulta de solo lectura: el empleado ve los activos que tiene (o tuvo) asignados. */
@WebServlet("/mis-activos")
public class MisActivosServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient AsignacionService asignaciones = new AsignacionService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Usuario u = (Usuario) req.getSession().getAttribute("usuario");
        if (u.getIdEmpleado() != null) {
            req.setAttribute("asignaciones", asignaciones.porEmpleado(u.getIdEmpleado()));
        }
        req.getRequestDispatcher("/WEB-INF/views/mis-activos.jsp").forward(req, resp);
    }
}
