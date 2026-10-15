package pe.edu.utp.gestoractivos.fake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.dao.UsuarioDAO;
import pe.edu.utp.gestoractivos.dto.UsuarioDTO;
import pe.edu.utp.gestoractivos.model.Usuario;

public class FakeUsuarioDAO implements UsuarioDAO {
    public final Map<Integer, Usuario> usuarios = new LinkedHashMap<>();
    private int sig = 1;

    public Usuario agregar(String username, String hash, String rol, Integer idEmpleado) {
        Usuario u = new Usuario();
        u.setIdUsuario(sig++);
        u.setUsername(username);
        u.setPasswordHash(hash);
        u.setRol(rol);
        u.setIdEmpleado(idEmpleado);
        usuarios.put(u.getIdUsuario(), u);
        return u;
    }

    @Override
    public Usuario buscarPorUsername(String username) {
        return usuarios.values().stream().filter(u -> u.getUsername().equals(username) && u.isActivo())
                .findFirst().orElse(null);
    }
    @Override
    public Usuario buscarPorId(int id) { return usuarios.get(id); }
    @Override
    public List<UsuarioDTO> listar() { return new ArrayList<>(); }
    @Override
    public boolean existeUsername(String username) {
        return usuarios.values().stream().anyMatch(u -> u.getUsername().equals(username));
    }
    @Override
    public boolean empleadoTieneUsuario(int idEmpleado) {
        return usuarios.values().stream().anyMatch(u -> u.getIdEmpleado() != null && u.getIdEmpleado() == idEmpleado);
    }
    @Override
    public int insertar(String username, String passwordHash, String rol, Integer idEmpleado) {
        return agregar(username, passwordHash, rol, idEmpleado).getIdUsuario();
    }
    @Override
    public void cambiarActivo(int id, boolean activo) { usuarios.get(id).setActivo(activo); }
    @Override
    public void cambiarPassword(int id, String hash) { usuarios.get(id).setPasswordHash(hash); }
    @Override
    public int contarAdministradoresActivos() {
        return (int) usuarios.values().stream().filter(u -> u.isAdministrador() && u.isActivo()).count();
    }
}
