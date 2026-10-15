package pe.edu.utp.gestoractivos.dao;

import java.util.List;
import pe.edu.utp.gestoractivos.dto.AsignacionDTO;

public interface AsignacionDAO {
    /**
     * Registra la asignación, pasa el activo a ASIGNADO y deja traza en el historial
     * dentro de una misma transacción. Devuelve false si el activo ya no estaba DISPONIBLE.
     */
    boolean asignar(int idActivo, int idEmpleado, int idUsuario, String observacion);
    /** Cierra la asignación vigente y devuelve el activo a DISPONIBLE (transacción). */
    boolean devolver(int idActivo, int idUsuario);
    List<AsignacionDTO> listarVigentes();
    List<AsignacionDTO> listarPorEmpleado(int idEmpleado);
}
