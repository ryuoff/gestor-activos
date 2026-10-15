package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.service.EmpleadoService;
import pe.edu.utp.gestoractivos.util.NegocioException;

/**
 *   GET  /empleados            -> lista
 *   GET  /empleados/nuevo      -> formulario de registro
 *   GET  /empleados/editar?id= -> formulario de edición
 *   POST /empleados/guardar    -> inserta o actualiza
 *   POST /empleados/estado     -> activa o desactiva
 */
@WebServlet("/empleados/*")
public class EmpleadoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient EmpleadoService service = new EmpleadoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (accion) {
            case "/nuevo" -> formulario(req, resp, new Empleado());
            case "/editar" -> {
                Empleado e = service.buscar(Mensajes.entero(req.getParameter("id")));
                if (e == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Empleado no encontrado");
                } else {
                    formulario(req, resp, e);
                }
            }
            default -> {
                Mensajes.mostrar(req);
                req.setAttribute("empleados", service.listar());
                req.getRequestDispatcher("/WEB-INF/views/empleados.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        try {
            switch (accion) {
                case "/guardar" -> guardar(req, resp);
                case "/estado" -> {
                    boolean activo = Boolean.parseBoolean(req.getParameter("activo"));
                    service.cambiarActivo(Mensajes.entero(req.getParameter("id")), activo);
                    Mensajes.ok(req, activo ? "Empleado activado." : "Empleado desactivado.");
                    resp.sendRedirect(req.getContextPath() + "/empleados");
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NegocioException e) {
            Mensajes.error(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/empleados");
        }
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        Empleado e = new Empleado();
        e.setIdEmpleado(Mensajes.entero(req.getParameter("idEmpleado")));
        e.setDni(req.getParameter("dni"));
        e.setNombres(req.getParameter("nombres"));
        e.setApellidos(req.getParameter("apellidos"));
        e.setCorreo(req.getParameter("correo"));
        e.setArea(req.getParameter("area"));
        try {
            if (e.getIdEmpleado() == 0) {
                service.registrar(e);
                Mensajes.ok(req, "Empleado registrado correctamente.");
            } else {
                service.editar(e);
                Mensajes.ok(req, "Empleado actualizado correctamente.");
            }
            resp.sendRedirect(req.getContextPath() + "/empleados");
        } catch (NegocioException ex) {
            req.setAttribute("error", ex.getMessage());
            formulario(req, resp, e);          // conserva lo digitado
        }
    }

    private void formulario(HttpServletRequest req, HttpServletResponse resp, Empleado e)
            throws ServletException, IOException {
        req.setAttribute("empleado", e);
        req.getRequestDispatcher("/WEB-INF/views/empleado-form.jsp").forward(req, resp);
    }
}
