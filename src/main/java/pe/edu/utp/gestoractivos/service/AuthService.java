package pe.edu.utp.gestoractivos.service;

import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dao.UsuarioDAO;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.util.LoginThrottle;
import pe.edu.utp.gestoractivos.util.NegocioException;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

public class AuthService {
    /** Hash ficticio para gastar el mismo tiempo cuando el usuario no existe (evita enumerar cuentas). */
    private static final String HASH_FALSO = PasswordUtil.hash("no-existe");

    private static final LoginThrottle THROTTLE_COMPARTIDO = new LoginThrottle();

    private final UsuarioDAO usuarioDAO;
    private final LoginThrottle throttle;

    public AuthService() {
        this(DAOFactory.getUsuarioDAO(), THROTTLE_COMPARTIDO);
    }

    public AuthService(UsuarioDAO usuarioDAO, LoginThrottle throttle) {
        this.usuarioDAO = usuarioDAO;
        this.throttle = throttle;
    }

    /**
     * Devuelve el usuario si las credenciales son correctas; null si no lo son.
     * Lanza NegocioException si el usuario/IP está bloqueado por demasiados intentos.
     */
    public Usuario autenticar(String username, String password, String ip) throws NegocioException {
        if (username == null || username.isBlank() || password == null || password.isEmpty()) {
            return null;
        }
        String clave = username.trim().toLowerCase() + "|" + ip;
        if (throttle.bloqueado(clave)) {
            long min = (throttle.segundosRestantes(clave) + 59) / 60;
            throw new NegocioException("Demasiados intentos fallidos. Vuelve a intentarlo en " + min
                    + (min == 1 ? " minuto." : " minutos."));
        }
        Usuario u = usuarioDAO.buscarPorUsername(username.trim());
        boolean ok = PasswordUtil.verificar(password, u != null ? u.getPasswordHash() : HASH_FALSO);
        if (u != null && ok) {
            throttle.registrarExito(clave);
            return u;
        }
        throttle.registrarFallo(clave);
        return null;
    }
}
