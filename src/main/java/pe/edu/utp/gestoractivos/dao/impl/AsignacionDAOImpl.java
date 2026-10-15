package pe.edu.utp.gestoractivos.dao.impl;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Timestamp;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.AsignacionDAO;
import pe.edu.utp.gestoractivos.dto.AsignacionDTO;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.util.ConexionBD;
import pe.edu.utp.gestoractivos.util.DaoException;

public class AsignacionDAOImpl implements AsignacionDAO {

    private static final String SQL_BASE = """
            SELECT s.id_asignacion, s.id_activo, a.codigo, a.nombre AS activo,
                   CONCAT(e.nombres, ' ', e.apellidos) AS empleado, e.area,
                   s.fecha_asignacion, s.fecha_devolucion, s.observacion
              FROM asignacion s
              JOIN activo a ON a.id_activo = s.id_activo
              JOIN empleado e ON e.id_empleado = s.id_empleado
            """;

    @Override
    public boolean asignar(int idActivo, int idEmpleado, int idUsuario, String observacion) {
        try (Connection cn = ConexionBD.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                // El UPDATE condicionado evita asignar dos veces el mismo activo en paralelo.
                int filas;
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE activo SET estado = 'ASIGNADO' WHERE id_activo = ? AND estado = 'DISPONIBLE'")) {
                    ps.setInt(1, idActivo);
                    filas = ps.executeUpdate();
                }
                if (filas == 0) {
                    cn.rollback();
                    return false;
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "INSERT INTO asignacion (id_activo, id_empleado, id_usuario_registra, observacion) "
                      + "VALUES (?, ?, ?, ?)")) {
                    ps.setInt(1, idActivo);
                    ps.setInt(2, idEmpleado);
                    ps.setInt(3, idUsuario);
                    ps.setString(4, observacion);
                    ps.executeUpdate();
                }
                ActivoDAOImpl.registrarHistorial(cn, idActivo, EstadoActivo.DISPONIBLE, EstadoActivo.ASIGNADO,
                        idUsuario, "Asignación a empleado #" + idEmpleado);
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al asignar activo", e);
        }
    }

    @Override
    public boolean devolver(int idActivo, int idUsuario) {
        try (Connection cn = ConexionBD.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                int filas;
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE asignacion SET fecha_devolucion = ? WHERE id_activo = ? AND fecha_devolucion IS NULL")) {
                    ps.setTimestamp(1, new Timestamp(System.currentTimeMillis()));
                    ps.setInt(2, idActivo);
                    filas = ps.executeUpdate();
                }
                if (filas == 0) {
                    cn.rollback();
                    return false;
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE activo SET estado = 'DISPONIBLE' WHERE id_activo = ?")) {
                    ps.setInt(1, idActivo);
                    ps.executeUpdate();
                }
                ActivoDAOImpl.registrarHistorial(cn, idActivo, EstadoActivo.ASIGNADO, EstadoActivo.DISPONIBLE,
                        idUsuario, "Devolución del activo");
                cn.commit();
                return true;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al devolver activo", e);
        }
    }

    @Override
    public List<AsignacionDTO> listarVigentes() {
        return consultar(SQL_BASE + " WHERE s.fecha_devolucion IS NULL ORDER BY s.fecha_asignacion DESC", null);
    }

    @Override
    public List<AsignacionDTO> listarPorEmpleado(int idEmpleado) {
        return consultar(SQL_BASE + " WHERE s.id_empleado = ? ORDER BY s.fecha_asignacion DESC", idEmpleado);
    }

    private List<AsignacionDTO> consultar(String sql, Integer parametro) {
        List<AsignacionDTO> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (parametro != null) {
                ps.setInt(1, parametro);
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    AsignacionDTO d = new AsignacionDTO();
                    d.setIdAsignacion(rs.getInt("id_asignacion"));
                    d.setIdActivo(rs.getInt("id_activo"));
                    d.setCodigoActivo(rs.getString("codigo"));
                    d.setNombreActivo(rs.getString("activo"));
                    d.setEmpleado(rs.getString("empleado"));
                    d.setArea(rs.getString("area"));
                    d.setFechaAsignacion(rs.getTimestamp("fecha_asignacion").toLocalDateTime());
                    Timestamp dev = rs.getTimestamp("fecha_devolucion");
                    d.setFechaDevolucion(dev == null ? null : dev.toLocalDateTime());
                    d.setObservacion(rs.getString("observacion"));
                    lista.add(d);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al consultar asignaciones", e);
        }
    }
}
