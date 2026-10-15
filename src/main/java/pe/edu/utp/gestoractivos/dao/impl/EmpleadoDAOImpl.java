package pe.edu.utp.gestoractivos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.EmpleadoDAO;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.util.ConexionBD;
import pe.edu.utp.gestoractivos.util.DaoException;

public class EmpleadoDAOImpl implements EmpleadoDAO {

    private static final String SELECT = "SELECT id_empleado, dni, nombres, apellidos, correo, area, activo FROM empleado e ";
    private static final String ORDEN = " ORDER BY apellidos, nombres";

    @Override
    public List<Empleado> listarActivos() {
        return listar(SELECT + "WHERE activo = TRUE" + ORDEN);
    }

    @Override
    public List<Empleado> listarTodos() {
        return listar(SELECT + ORDEN);
    }

    @Override
    public List<Empleado> listarSinUsuario() {
        return listar(SELECT + "WHERE activo = TRUE AND NOT EXISTS "
                    + "(SELECT 1 FROM usuario u WHERE u.id_empleado = e.id_empleado)" + ORDEN);
    }

    private List<Empleado> listar(String sql) {
        List<Empleado> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery(sql)) {
            while (rs.next()) {
                lista.add(mapear(rs));
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al listar empleados", e);
        }
    }

    @Override
    public Empleado buscarPorId(int idEmpleado) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(SELECT + "WHERE id_empleado = ?")) {
            ps.setInt(1, idEmpleado);
            try (ResultSet rs = ps.executeQuery()) {
                return rs.next() ? mapear(rs) : null;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar empleado", e);
        }
    }

    @Override
    public boolean existeDni(String dni, int excluirId) {
        return contar("SELECT COUNT(*) FROM empleado WHERE dni = ? AND id_empleado <> ?", dni, excluirId) > 0;
    }

    @Override
    public boolean existeCorreo(String correo, int excluirId) {
        return contar("SELECT COUNT(*) FROM empleado WHERE correo = ? AND id_empleado <> ?", correo, excluirId) > 0;
    }

    @Override
    public int contarAsignacionesVigentes(int idEmpleado) {
        return contar("SELECT COUNT(*) FROM asignacion WHERE id_empleado = ? AND fecha_devolucion IS NULL",
                idEmpleado, null);
    }

    private int contar(String sql, Object a, Object b) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setObject(1, a);
            if (b != null) {
                ps.setObject(2, b);
            }
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1);
            }
        } catch (SQLException e) {
            throw new DaoException("Error al consultar empleados", e);
        }
    }

    @Override
    public int insertar(Empleado e) {
        String sql = "INSERT INTO empleado (dni, nombres, apellidos, correo, area) VALUES (?, ?, ?, ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
            ps.setString(1, e.getDni());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getCorreo());
            ps.setString(5, e.getArea());
            ps.executeUpdate();
            try (ResultSet keys = ps.getGeneratedKeys()) {
                keys.next();
                return keys.getInt(1);
            }
        } catch (SQLException ex) {
            throw new DaoException("Error al registrar empleado", ex);
        }
    }

    @Override
    public void actualizar(Empleado e) {
        String sql = "UPDATE empleado SET dni = ?, nombres = ?, apellidos = ?, correo = ?, area = ? WHERE id_empleado = ?";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, e.getDni());
            ps.setString(2, e.getNombres());
            ps.setString(3, e.getApellidos());
            ps.setString(4, e.getCorreo());
            ps.setString(5, e.getArea());
            ps.setInt(6, e.getIdEmpleado());
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error al actualizar empleado", ex);
        }
    }

    @Override
    public void cambiarActivo(int idEmpleado, boolean activo) {
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement("UPDATE empleado SET activo = ? WHERE id_empleado = ?")) {
            ps.setBoolean(1, activo);
            ps.setInt(2, idEmpleado);
            ps.executeUpdate();
        } catch (SQLException ex) {
            throw new DaoException("Error al cambiar el estado del empleado", ex);
        }
    }

    private Empleado mapear(ResultSet rs) throws SQLException {
        Empleado e = new Empleado();
        e.setIdEmpleado(rs.getInt("id_empleado"));
        e.setDni(rs.getString("dni"));
        e.setNombres(rs.getString("nombres"));
        e.setApellidos(rs.getString("apellidos"));
        e.setCorreo(rs.getString("correo"));
        e.setArea(rs.getString("area"));
        e.setActivo(rs.getBoolean("activo"));
        return e;
    }
}
