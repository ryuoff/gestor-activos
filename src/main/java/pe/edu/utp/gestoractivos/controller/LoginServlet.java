package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import java.io.IOException;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.AuthService;
import java.util.logging.Level;
import java.util.logging.Logger;
import pe.edu.utp.gestoractivos.util.NegocioException;

@WebServlet("/login")
public class LoginServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private static final Logger LOG = Logger.getLogger(LoginServlet.class.getName());
    private final transient AuthService auth = new AuthService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.getRequestDispatcher("/WEB-INF/views/login.jsp").forward(req, resp);
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        try {
            Usuario u = auth.autenticar(req.getParameter("username"), req.getParameter("password"), req.getRemoteAddr());
            if (u == null) {
                req.setAttribute("error", "Usuario o contraseña incorrectos.");
                doGet(req, resp);
                return;
            }
            req.changeSessionId();                                   // evita fijación de sesión
            u.setPasswordHash(null);                                 // el hash no se guarda en la sesión
            req.getSession().setAttribute("usuario", u);
            resp.sendRedirect(req.getContextPath() + (u.isAdministrador() ? "/inicio" : "/mis-activos"));
        } catch (NegocioException e) {
            req.setAttribute("error", e.getMessage());
            doGet(req, resp);
        } catch (RuntimeException | LinkageError e) {
            // Normalmente: MySQL apagado, base inexistente o usuario/clave incorrectos en db.properties.
            LOG.log(Level.SEVERE, "No se pudo validar el inicio de sesión", e);
            req.setAttribute("error", "No se pudo conectar con la base de datos. Revisa que MySQL esté encendido, "
                    + "que exista la base gestor_activos y que db.properties tenga el usuario y la clave correctos.");
            doGet(req, resp);
        }
    }
}
