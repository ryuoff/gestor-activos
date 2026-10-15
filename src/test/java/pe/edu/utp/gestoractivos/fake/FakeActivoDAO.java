package pe.edu.utp.gestoractivos.fake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.dao.ActivoDAO;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.dto.HistorialDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

/** DAO en memoria para probar la capa de servicio sin base de datos. */
public class FakeActivoDAO implements ActivoDAO {
    public final Map<Integer, Activo> activos = new LinkedHashMap<>();
    public final Map<Integer, String> responsables = new LinkedHashMap<>();
    public final List<String> historial = new ArrayList<>();
    private int sig = 1;

    public Activo agregar(String codigo, EstadoActivo estado) {
        Activo a = new Activo();
        a.setIdActivo(sig++);
        a.setCodigo(codigo);
        a.setNombre("Activo " + codigo);
        a.setIdTipo(1);
        a.setEstado(estado);
        a.setFechaAdquisicion(java.time.LocalDate.of(2024, 1, 10));
        a.setValor(new java.math.BigDecimal("100.00"));
        activos.put(a.getIdActivo(), a);
        return a;
    }

    @Override
    public List<ActivoDTO> listar(EstadoActivo estado) {
        List<ActivoDTO> lista = new ArrayList<>();
        for (Activo a : activos.values()) {
            if (estado != null && a.getEstado() != estado) {
                continue;
            }
            ActivoDTO d = new ActivoDTO();
            d.setIdActivo(a.getIdActivo());
            d.setCodigo(a.getCodigo());
            d.setNombre(a.getNombre());
            d.setTipo(a.getIdTipo() == 1 ? "Equipo" : "Otro");
            d.setEstado(a.getEstado());
            d.setFechaAdquisicion(a.getFechaAdquisicion());
            d.setValor(a.getValor());
            d.setResponsable(responsables.get(a.getIdActivo()));
            lista.add(d);
        }
        return lista;
    }

    @Override
    public Activo buscarPorId(int id) { return activos.get(id); }

    @Override
    public boolean existeCodigo(String codigo, int excluirId) {
        return activos.values().stream().anyMatch(a -> a.getCodigo().equals(codigo) && a.getIdActivo() != excluirId);
    }

    @Override
    public int insertar(Activo a, int idUsuario) {
        a.setIdActivo(sig++);
        a.setEstado(EstadoActivo.DISPONIBLE);
        activos.put(a.getIdActivo(), a);
        historial.add("INSERT " + a.getCodigo());
        return a.getIdActivo();
    }

    @Override
    public void actualizar(Activo a) { activos.put(a.getIdActivo(), a); }

    @Override
    public void cambiarEstado(int idActivo, EstadoActivo nuevo, int idUsuario, String detalle) {
        Activo a = activos.get(idActivo);
        historial.add(a.getEstado() + "->" + nuevo);
        a.setEstado(nuevo);
    }

    @Override
    public List<HistorialDTO> historial(int idActivo) { return new ArrayList<>(); }

    @Override
    public List<HistorialDTO> movimientosRecientes(int limite) { return new ArrayList<>(); }
}
