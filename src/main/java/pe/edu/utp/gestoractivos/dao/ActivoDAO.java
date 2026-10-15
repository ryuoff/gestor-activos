package pe.edu.utp.gestoractivos.dao;

import java.util.List;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.dto.HistorialDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

public interface ActivoDAO {
    /** Lista activos; si estado es null no filtra. */
    List<ActivoDTO> listar(EstadoActivo estado);
    Activo buscarPorId(int idActivo);
    boolean existeCodigo(String codigo, int excluirId);
    /** Inserta el activo y registra el primer movimiento en el historial (transacción). */
    int insertar(Activo activo, int idUsuario);
    /** Actualiza datos generales (no el estado). */
    void actualizar(Activo activo);
    /** Cambia el estado y registra el movimiento en el historial (transacción). */
    void cambiarEstado(int idActivo, EstadoActivo nuevo, int idUsuario, String detalle);
    List<HistorialDTO> historial(int idActivo);
    /** Últimos movimientos de estado de todos los activos (más recientes primero). */
    List<HistorialDTO> movimientosRecientes(int limite);
}
