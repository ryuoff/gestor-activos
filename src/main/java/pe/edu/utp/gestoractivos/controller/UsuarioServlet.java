package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.UsuarioService;
import pe.edu.utp.gestoractivos.util.NegocioException;

/**
 *   GET  /usuarios           -> lista de cuentas
 *   GET  /usuarios/nuevo     -> crear cuenta
 *   GET  /usuarios/clave?id= -> restablecer contraseña
 *   POST /usuarios/guardar   -> crea la cuenta
 *   POST /usuarios/estado    -> activa o desactiva
 *   POST /usuarios/clave     -> guarda la nueva contraseña
 */
@WebServlet("/usuarios/*")
public class UsuarioServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient UsuarioService service = new UsuarioService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (accion) {
            case "/nuevo" -> formularioNuevo(req, resp, "", "EMPLEADO", 0);
            case "/clave" -> {
                Usuario u = service.buscar(Mensajes.entero(req.getParameter("id")));
                if (u == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Usuario no encontrado");
                } else {
                    req.setAttribute("cuenta", u);
                    req.getRequestDispatcher("/WEB-INF/views/usuario-clave.jsp").forward(req, resp);
                }
            }
            default -> {
                Mensajes.mostrar(req);
                req.setAttribute("usuarios", service.listar());
                req.getRequestDispatcher("/WEB-INF/views/usuarios.jsp").forward(req, resp);
            }
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        Usuario actual = (Usuario) req.getSession().getAttribute("usuario");
        try {
            switch (accion) {
                case "/guardar" -> {
                    String username = req.getParameter("username");
                    String rol = req.getParameter("rol");
                    int idEmp = Mensajes.entero(req.getParameter("idEmpleado"));
                    try {
                        service.crear(username, req.getParameter("password"), rol, idEmp == 0 ? null : idEmp);
                    } catch (NegocioException e) {
                        req.setAttribute("error", e.getMessage());
                        formularioNuevo(req, resp, username, rol, idEmp);
                        return;
                    }
                    Mensajes.ok(req, "Usuario creado correctamente.");
                    resp.sendRedirect(req.getContextPath() + "/usuarios");
                }
                case "/estado" -> {
                    boolean activo = Boolean.parseBoolean(req.getParameter("activo"));
                    service.cambiarActivo(Mensajes.entero(req.getParameter("id")), activo, actual.getIdUsuario());
                    Mensajes.ok(req, activo ? "Usuario activado." : "Usuario desactivado.");
                    resp.sendRedirect(req.getContextPath() + "/usuarios");
                }
                case "/clave" -> {
                    int id = Mensajes.entero(req.getParameter("id"));
                    try {
                        service.cambiarPassword(id, req.getParameter("password"));
                    } catch (NegocioException e) {
                        req.setAttribute("error", e.getMessage());
                        req.setAttribute("cuenta", service.buscar(id));
                        req.getRequestDispatcher("/WEB-INF/views/usuario-clave.jsp").forward(req, resp);
                        return;
                    }
                    Mensajes.ok(req, "Contraseña actualizada.");
                    resp.sendRedirect(req.getContextPath() + "/usuarios");
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NegocioException e) {
            Mensajes.error(req, e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/usuarios");
        }
    }

    private void formularioNuevo(HttpServletRequest req, HttpServletResponse resp, String username, String rol,
                                 int idEmpleado) throws ServletException, IOException {
        req.setAttribute("username", username);
        req.setAttribute("rol", rol);
        req.setAttribute("idEmpleado", idEmpleado);
        req.setAttribute("empleados", service.empleadosSinUsuario());
        req.getRequestDispatcher("/WEB-INF/views/usuario-form.jsp").forward(req, resp);
    }
}
