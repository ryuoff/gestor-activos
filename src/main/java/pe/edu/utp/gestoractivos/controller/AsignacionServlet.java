package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.ActivoService;
import pe.edu.utp.gestoractivos.service.AsignacionService;
import pe.edu.utp.gestoractivos.util.NegocioException;

/**
 *   GET  /asignaciones          -> asignaciones vigentes + formulario para asignar
 *   POST /asignaciones/asignar  -> registra una nueva asignación
 *   POST /asignaciones/devolver -> registra la devolución
 */
@WebServlet("/asignaciones/*")
public class AsignacionServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient AsignacionService asignaciones = new AsignacionService();
    private final transient ActivoService activos = new ActivoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        HttpSession s = req.getSession();
        for (String clave : new String[]{"mensaje", "error"}) {
            Object v = s.getAttribute(clave);
            if (v != null) {
                req.setAttribute(clave, v);
                s.removeAttribute(clave);
            }
        }
        req.setAttribute("vigentes", asignaciones.vigentes());
        req.setAttribute("disponibles", activos.listar(EstadoActivo.DISPONIBLE));
        req.setAttribute("empleados", asignaciones.empleados());
        req.getRequestDispatcher("/WEB-INF/views/asignaciones.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws IOException {
        req.setCharacterEncoding("UTF-8");
        HttpSession s = req.getSession();
        Usuario u = (Usuario) s.getAttribute("usuario");
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            switch (accion) {
                case "/asignar" -> {
                    asignaciones.asignar(entero(req.getParameter("idActivo")), entero(req.getParameter("idEmpleado")),
                            u.getIdUsuario(), req.getParameter("observacion"));
                    s.setAttribute("mensaje", "Activo asignado correctamente.");
                }
                case "/devolver" -> {
                    asignaciones.devolver(entero(req.getParameter("idActivo")), u.getIdUsuario());
                    s.setAttribute("mensaje", "Devolución registrada; el activo vuelve a estar disponible.");
                }
                default -> {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND);
                    return;
                }
            }
        } catch (NegocioException e) {
            s.setAttribute("error", e.getMessage());
        }
        resp.sendRedirect(req.getContextPath() + "/asignaciones");
    }

    private static int entero(String v) {
        try {
            return Integer.parseInt(v);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
