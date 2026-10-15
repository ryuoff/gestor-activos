package pe.edu.utp.gestoractivos.service;

import java.util.List;
import java.util.Set;
import java.util.regex.Pattern;
import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dao.EmpleadoDAO;
import pe.edu.utp.gestoractivos.dao.UsuarioDAO;
import pe.edu.utp.gestoractivos.dto.UsuarioDTO;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.util.NegocioException;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

public class UsuarioService {
    private static final Pattern USERNAME = Pattern.compile("[A-Za-z0-9._-]{3,40}");
    private static final Set<String> ROLES = Set.of("ADMINISTRADOR", "EMPLEADO");

    private final UsuarioDAO usuarioDAO;
    private final EmpleadoDAO empleadoDAO;

    public UsuarioService() {
        this(DAOFactory.getUsuarioDAO(), DAOFactory.getEmpleadoDAO());
    }

    public UsuarioService(UsuarioDAO usuarioDAO, EmpleadoDAO empleadoDAO) {
        this.usuarioDAO = usuarioDAO;
        this.empleadoDAO = empleadoDAO;
    }

    public List<UsuarioDTO> listar() { return usuarioDAO.listar(); }
    public Usuario buscar(int id) { return usuarioDAO.buscarPorId(id); }
    public List<Empleado> empleadosSinUsuario() { return empleadoDAO.listarSinUsuario(); }

    public int crear(String username, String password, String rol, Integer idEmpleado) throws NegocioException {
        username = username == null ? "" : username.trim();
        if (!USERNAME.matcher(username).matches()) {
            throw new NegocioException("El usuario debe tener entre 3 y 40 caracteres: letras, números, punto, guion o guion bajo.");
        }
        validarPassword(password);
        if (rol == null || !ROLES.contains(rol)) {
            throw new NegocioException("Selecciona un rol válido.");
        }
        if ("EMPLEADO".equals(rol) && idEmpleado == null) {
            throw new NegocioException("Un usuario con rol Empleado debe estar vinculado a un empleado.");
        }
        if (idEmpleado != null) {
            Empleado e = empleadoDAO.buscarPorId(idEmpleado);
            if (e == null || !e.isActivo()) {
                throw new NegocioException("El empleado seleccionado no existe o está desactivado.");
            }
            if (usuarioDAO.empleadoTieneUsuario(idEmpleado)) {
                throw new NegocioException("Ese empleado ya tiene una cuenta de acceso.");
            }
        }
        if (usuarioDAO.existeUsername(username)) {
            throw new NegocioException("El usuario \"" + username + "\" ya existe.");
        }
        return usuarioDAO.insertar(username, PasswordUtil.hash(password), rol, idEmpleado);
    }

    /** No se puede desactivar la propia cuenta ni al último administrador activo. */
    public void cambiarActivo(int idUsuario, boolean activo, int idUsuarioActual) throws NegocioException {
        Usuario u = usuarioDAO.buscarPorId(idUsuario);
        if (u == null) {
            throw new NegocioException("El usuario no existe.");
        }
        if (!activo) {
            if (idUsuario == idUsuarioActual) {
                throw new NegocioException("No puedes desactivar tu propia cuenta.");
            }
            if (u.isAdministrador() && u.isActivo() && usuarioDAO.contarAdministradoresActivos() <= 1) {
                throw new NegocioException("Debe quedar al menos un administrador activo.");
            }
        }
        usuarioDAO.cambiarActivo(idUsuario, activo);
    }

    public void cambiarPassword(int idUsuario, String nueva) throws NegocioException {
        if (usuarioDAO.buscarPorId(idUsuario) == null) {
            throw new NegocioException("El usuario no existe.");
        }
        validarPassword(nueva);
        usuarioDAO.cambiarPassword(idUsuario, PasswordUtil.hash(nueva));
    }

    private void validarPassword(String p) throws NegocioException {
        if (p == null || p.length() < 8 || !p.matches(".*[A-Za-z].*") || !p.matches(".*\\d.*")) {
            throw new NegocioException("La contraseña debe tener al menos 8 caracteres, con letras y números.");
        }
    }
}
