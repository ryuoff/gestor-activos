package pe.edu.utp.gestoractivos.dao.impl;

import java.sql.Connection;
import java.sql.Date;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.ActivoDAO;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.dto.HistorialDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.util.ConexionBD;
import pe.edu.utp.gestoractivos.util.DaoException;

public class ActivoDAOImpl implements ActivoDAO {

    private static final String SQL_LISTAR = """
            SELECT a.id_activo, a.codigo, a.nombre, a.descripcion, t.nombre AS tipo, a.estado,
                   a.fecha_adquisicion, a.valor,
                   CONCAT(e.nombres, ' ', e.apellidos) AS responsable
              FROM activo a
              JOIN tipo_activo t ON t.id_tipo = a.id_tipo
              LEFT JOIN asignacion s ON s.id_activo = a.id_activo AND s.fecha_devolucion IS NULL
              LEFT JOIN empleado e ON e.id_empleado = s.id_empleado
            """;

    @Override
    public List<ActivoDTO> listar(EstadoActivo estado) {
        String sql = SQL_LISTAR + (estado != null ? " WHERE a.estado = ?" : "") + " ORDER BY a.codigo";
        List<ActivoDTO> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            if (estado != null) {
                ps.setString(1, estado.name());
            }
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    ActivoDTO d = new ActivoDTO();
                    d.setIdActivo(rs.getInt("id_activo"));
                    d.setCodigo(rs.getString("codigo"));
                    d.setNombre(rs.getString("nombre"));
                    d.setDescripcion(rs.getString("descripcion"));
                    d.setTipo(rs.getString("tipo"));
                    d.setEstado(EstadoActivo.valueOf(rs.getString("estado")));
                    d.setFechaAdquisicion(rs.getDate("fecha_adquisicion").toLocalDate());
                    d.setValor(rs.getBigDecimal("valor"));
                    d.setResponsable(rs.getString("responsable"));
                    lista.add(d);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al listar activos", e);
        }
    }

    @Override
    public Activo buscarPorId(int idActivo) {
        String sql = "SELECT id_activo, codigo, nombre, descripcion, id_tipo, estado, fecha_adquisicion, valor "
                   + "FROM activo WHERE id_activo = ?";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idActivo);
            try (ResultSet rs = ps.executeQuery()) {
                if (!rs.next()) {
                    return null;
                }
                Activo a = new Activo();
                a.setIdActivo(rs.getInt("id_activo"));
                a.setCodigo(rs.getString("codigo"));
                a.setNombre(rs.getString("nombre"));
                a.setDescripcion(rs.getString("descripcion"));
                a.setIdTipo(rs.getInt("id_tipo"));
                a.setEstado(EstadoActivo.valueOf(rs.getString("estado")));
                a.setFechaAdquisicion(rs.getDate("fecha_adquisicion").toLocalDate());
                a.setValor(rs.getBigDecimal("valor"));
                return a;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al buscar activo", e);
        }
    }

    @Override
    public boolean existeCodigo(String codigo, int excluirId) {
        String sql = "SELECT COUNT(*) FROM activo WHERE codigo = ? AND id_activo <> ?";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, codigo);
            ps.setInt(2, excluirId);
            try (ResultSet rs = ps.executeQuery()) {
                rs.next();
                return rs.getInt(1) > 0;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al validar código", e);
        }
    }

    @Override
    public int insertar(Activo a, int idUsuario) {
        String sql = "INSERT INTO activo (codigo, nombre, descripcion, id_tipo, estado, fecha_adquisicion, valor) "
                   + "VALUES (?, ?, ?, ?, 'DISPONIBLE', ?, ?)";
        try (Connection cn = ConexionBD.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try (PreparedStatement ps = cn.prepareStatement(sql, Statement.RETURN_GENERATED_KEYS)) {
                ps.setString(1, a.getCodigo());
                ps.setString(2, a.getNombre());
                ps.setString(3, a.getDescripcion());
                ps.setInt(4, a.getIdTipo());
                ps.setDate(5, Date.valueOf(a.getFechaAdquisicion()));
                ps.setBigDecimal(6, a.getValor());
                ps.executeUpdate();
                int id;
                try (ResultSet keys = ps.getGeneratedKeys()) {
                    keys.next();
                    id = keys.getInt(1);
                }
                registrarHistorial(cn, id, null, EstadoActivo.DISPONIBLE, idUsuario, "Registro inicial");
                cn.commit();
                return id;
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al registrar activo", e);
        }
    }

    @Override
    public void actualizar(Activo a) {
        String sql = "UPDATE activo SET codigo = ?, nombre = ?, descripcion = ?, id_tipo = ?, "
                   + "fecha_adquisicion = ?, valor = ? WHERE id_activo = ?";
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setString(1, a.getCodigo());
            ps.setString(2, a.getNombre());
            ps.setString(3, a.getDescripcion());
            ps.setInt(4, a.getIdTipo());
            ps.setDate(5, Date.valueOf(a.getFechaAdquisicion()));
            ps.setBigDecimal(6, a.getValor());
            ps.setInt(7, a.getIdActivo());
            ps.executeUpdate();
        } catch (SQLException e) {
            throw new DaoException("Error al actualizar activo", e);
        }
    }

    @Override
    public void cambiarEstado(int idActivo, EstadoActivo nuevo, int idUsuario, String detalle) {
        try (Connection cn = ConexionBD.getInstancia().getConnection()) {
            cn.setAutoCommit(false);
            try {
                EstadoActivo anterior;
                try (PreparedStatement ps = cn.prepareStatement(
                        "SELECT estado FROM activo WHERE id_activo = ? FOR UPDATE")) {
                    ps.setInt(1, idActivo);
                    try (ResultSet rs = ps.executeQuery()) {
                        if (!rs.next()) {
                            throw new SQLException("Activo inexistente: " + idActivo);
                        }
                        anterior = EstadoActivo.valueOf(rs.getString(1));
                    }
                }
                try (PreparedStatement ps = cn.prepareStatement(
                        "UPDATE activo SET estado = ? WHERE id_activo = ?")) {
                    ps.setString(1, nuevo.name());
                    ps.setInt(2, idActivo);
                    ps.executeUpdate();
                }
                registrarHistorial(cn, idActivo, anterior, nuevo, idUsuario, detalle);
                cn.commit();
            } catch (SQLException e) {
                cn.rollback();
                throw e;
            }
        } catch (SQLException e) {
            throw new DaoException("Error al cambiar el estado del activo", e);
        }
    }

    @Override
    public List<HistorialDTO> historial(int idActivo) {
        String sql = """
                SELECT h.fecha_cambio, h.estado_anterior, h.estado_nuevo, u.username, h.detalle
                  FROM historial_estado h
                  JOIN usuario u ON u.id_usuario = h.id_usuario
                 WHERE h.id_activo = ?
                 ORDER BY h.fecha_cambio DESC, h.id_historial DESC
                """;
        List<HistorialDTO> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idActivo);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialDTO h = new HistorialDTO();
                    h.setFecha(rs.getTimestamp("fecha_cambio").toLocalDateTime());
                    h.setEstadoAnterior(rs.getString("estado_anterior"));
                    h.setEstadoNuevo(rs.getString("estado_nuevo"));
                    h.setUsuario(rs.getString("username"));
                    h.setDetalle(rs.getString("detalle"));
                    lista.add(h);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al consultar historial", e);
        }
    }

    @Override
    public List<HistorialDTO> movimientosRecientes(int limite) {
        String sql = """
                SELECT h.fecha_cambio, h.estado_anterior, h.estado_nuevo, u.username, h.detalle,
                       a.codigo, a.nombre
                  FROM historial_estado h
                  JOIN activo a ON a.id_activo = h.id_activo
                  JOIN usuario u ON u.id_usuario = h.id_usuario
                 ORDER BY h.fecha_cambio DESC, h.id_historial DESC
                 LIMIT ?
                """;
        List<HistorialDTO> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, limite);
            try (ResultSet rs = ps.executeQuery()) {
                while (rs.next()) {
                    HistorialDTO h = new HistorialDTO();
                    h.setFecha(rs.getTimestamp("fecha_cambio").toLocalDateTime());
                    h.setEstadoAnterior(rs.getString("estado_anterior"));
                    h.setEstadoNuevo(rs.getString("estado_nuevo"));
                    h.setUsuario(rs.getString("username"));
                    h.setDetalle(rs.getString("detalle"));
                    h.setCodigoActivo(rs.getString("codigo"));
                    h.setNombreActivo(rs.getString("nombre"));
                    lista.add(h);
                }
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al consultar movimientos recientes", e);
        }
    }

    /** Inserta una fila de trazabilidad usando la conexión (y transacción) recibida. */
    static void registrarHistorial(Connection cn, int idActivo, EstadoActivo anterior, EstadoActivo nuevo,
                                   int idUsuario, String detalle) throws SQLException {
        String sql = "INSERT INTO historial_estado (id_activo, estado_anterior, estado_nuevo, id_usuario, detalle) "
                   + "VALUES (?, ?, ?, ?, ?)";
        try (PreparedStatement ps = cn.prepareStatement(sql)) {
            ps.setInt(1, idActivo);
            ps.setString(2, anterior == null ? null : anterior.name());
            ps.setString(3, nuevo.name());
            ps.setInt(4, idUsuario);
            ps.setString(5, detalle);
            ps.executeUpdate();
        }
    }
}
