package pe.edu.utp.gestoractivos.dao;

import java.util.List;
import pe.edu.utp.gestoractivos.dto.UsuarioDTO;
import pe.edu.utp.gestoractivos.model.Usuario;

public interface UsuarioDAO {
    /** Usuario activo con ese username, o null si no existe o está desactivado. */
    Usuario buscarPorUsername(String username);
    Usuario buscarPorId(int idUsuario);
    List<UsuarioDTO> listar();
    boolean existeUsername(String username);
    boolean empleadoTieneUsuario(int idEmpleado);
    int insertar(String username, String passwordHash, String rol, Integer idEmpleado);
    void cambiarActivo(int idUsuario, boolean activo);
    void cambiarPassword(int idUsuario, String passwordHash);
    int contarAdministradoresActivos();
}
