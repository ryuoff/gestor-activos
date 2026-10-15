package pe.edu.utp.gestoractivos.fake;

import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.AsignacionDAO;
import pe.edu.utp.gestoractivos.dto.AsignacionDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

/** Imita las reglas transaccionales del DAO real: solo se asigna lo DISPONIBLE y solo hay una asignación vigente. */
public class FakeAsignacionDAO implements AsignacionDAO {
    private final FakeActivoDAO activos;
    public final List<int[]> vigentes = new ArrayList<>();   // {idActivo, idEmpleado}

    public FakeAsignacionDAO(FakeActivoDAO activos) { this.activos = activos; }

    @Override
    public boolean asignar(int idActivo, int idEmpleado, int idUsuario, String observacion) {
        Activo a = activos.buscarPorId(idActivo);
        if (a.getEstado() != EstadoActivo.DISPONIBLE) {
            return false;
        }
        a.setEstado(EstadoActivo.ASIGNADO);
        vigentes.add(new int[]{idActivo, idEmpleado});
        return true;
    }

    @Override
    public boolean devolver(int idActivo, int idUsuario) {
        boolean quitado = vigentes.removeIf(v -> v[0] == idActivo);
        if (quitado) {
            activos.buscarPorId(idActivo).setEstado(EstadoActivo.DISPONIBLE);
        }
        return quitado;
    }

    @Override
    public List<AsignacionDTO> listarVigentes() { return new ArrayList<>(); }
    @Override
    public List<AsignacionDTO> listarPorEmpleado(int idEmpleado) { return new ArrayList<>(); }
}
