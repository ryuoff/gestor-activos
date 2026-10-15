package pe.edu.utp.gestoractivos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.sql.Types;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.UsuarioDAO;
import pe.edu.utp.gestoractivos.dto.UsuarioDTO;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.util.ConexionBD;
import pe.edu.utp.gestoractivos.util.DaoException;

public class UsuarioDAOImpl implements UsuarioDAO {

    private static final String SQL_USUARIO = """
            SELECT u.id_usuario, u.username, u.password_hash, r.nombre AS rol, u.id_empleado, u.activo,
                   CONCAT(e.nombres, ' ', e.apellidos) AS nombre_completo
              FROM usuario u
              JOIN rol r ON r.id_rol = u.id_rol
              LEFT JOIN empleado e ON e.id_empleado = u.id_empleado
            """;

    @Override
    public Usuario buscarPorUsername(String username) {
        Usuario u = uno(SQL_USUARIO + " WHERE u.username = ?", username);
        return u != null && u.isActivo() ? u : null;
    }

    @Override
    public Usuario buscarPorId(int idUsuario) {
        return uno(SQL_USUARIO + " WHERE u.id_usuario = ?", idUsuario);
    }

    private Usuario uno(String sql, Object parametro) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Usuario u = new Usuario();
                u.setIdUsuario(rs.getInt("id_usuario"));
                u.setUsername(rs.getString("username"));
                u.setPasswordHash(rs.getString("password_hash"));
                u.setRol(rs.getString("rol"));
                int idEmp = rs.getInt("id_empleado");
                u.setIdEmpleado(rs.wasNull() ? null : idEmp);
                u.setNombreCompleto(rs.getString("nombre_completo"));
                u.setActivo(rs.getBoolean("activo"));
                return u;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar usuario", e);
        }
    }

    @Override
    public List<UsuarioDTO> listar() {
        String sql = """
                SELECT u.id_usuario, u.username, r.nombre AS rol, u.activo,
                       CONCAT(e.nombres, ' ', e.apellidos) AS empleado
                  FROM usuario u
                  JOIN rol r ON r.id_rol = u.id_rol
                  LEFT JOIN empleado e ON e.id_empleado = u.id_empleado
                 ORDER BY u.username
                """;
        List<UsuarioDTO> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                UsuarioDTO d = new UsuarioDTO();
                d.setIdUsuario(rs.getInt("id_usuario"));
                d.setUsername(rs.getString("username"));
                d.setRol(rs.getString("rol"));
                d.setEmpleado(rs.getString("empleado"));
                d.setActivo(rs.getBoolean("activo"));
                lista.add(d);
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al listar usuarios", e);
        }
    }

    @Override
    public boolean existeUsername(String username) {
        return contar("SELECT COUNT(*) FROM usuario WHERE username = ?", username) > 0;
    }

    @Override
    public boolean empleadoTieneUsuario(int idEmpleado) {
        return contar("SELECT COUNT(*) FROM usuario WHERE id_empleado = ?", idEmpleado) > 0;
    }

    @Override
    public int contarAdministradoresActivos() {
        return contar("SELECT COUNT(*) FROM usuario u JOIN rol r ON r.id_rol = u.id_rol "
                    + "WHERE r.nombre = ? AND u.activo = TRUE", "ADMINISTRADOR");
    }

    private int contar(String sql, Object parametro) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, parametro);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar usuarios", e);
        }
    }

    @Override
    public int insertar(String username, String passwordHash, String rol, Integer idEmpleado) {
        String sql = "INSERT INTO usuario (username, password_hash, id_rol, id_empleado) "
                   + "VALUES (?, ?, (SELECT id_rol FROM rol WHERE nombre = ?), ?)";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, username);
            ps.setString(2, passwordHash);
            ps.setString(3, rol);
            if (idEmpleado == null) {
                ps.setNull(4, Types.INTEGER);
            } else {
                ps.setInt(4, idEmpleado);
            }
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al crear usuario", e);
        }
    }

    @Override
    public void cambiarActivo(int idUsuario, boolean activo) {
        actualizar("UPDATE usuario SET activo = ? WHERE id_usuario = ?", activo, idUsuario);
    }

    @Override
    public void cambiarPassword(int idUsuario, String passwordHash) {
        actualizar("UPDATE usuario SET password_hash = ? WHERE id_usuario = ?", passwordHash, idUsuario);
    }

    private void actualizar(String sql, Object a, Object b) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, a);
            ps.setObject(2, b);
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar usuario", e);
        }
    }
}
