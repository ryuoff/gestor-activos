package pe.edu.utp.gestoractivos.service;

import java.math.BigDecimal;
import java.time.LocalDate;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.ActivoDAO;
import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dao.TipoActivoDAO;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.dto.HistorialDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.model.TipoActivo;
import pe.edu.utp.gestoractivos.util.NegocioException;

/** Capa de servicio: aquí viven las reglas de negocio definidas en el Avance 1. */
public class ActivoService {
    private final ActivoDAO activoDAO;
    private final TipoActivoDAO tipoDAO;

    public ActivoService() {
        this(DAOFactory.getActivoDAO(), DAOFactory.getTipoActivoDAO());
    }

    /** Constructor para pruebas: permite inyectar DAO falsos. */
    public ActivoService(ActivoDAO activoDAO, TipoActivoDAO tipoDAO) {
        this.activoDAO = activoDAO;
        this.tipoDAO = tipoDAO;
    }

    public List<ActivoDTO> listar(EstadoActivo filtro) { return activoDAO.listar(filtro); }
    public List<TipoActivo> tipos() { return tipoDAO.listar(); }
    public Activo buscar(int id) { return activoDAO.buscarPorId(id); }
    public List<HistorialDTO> historial(int id) { return activoDAO.historial(id); }

    public int registrar(Activo a, int idUsuario) throws NegocioException {
        validar(a, 0);
        return activoDAO.insertar(a, idUsuario);
    }

    public void editar(Activo a) throws NegocioException {
        Activo actual = activoDAO.buscarPorId(a.getIdActivo());
        if (actual == null) {
            throw new NegocioException("El activo no existe.");
        }
        if (actual.getEstado() == EstadoActivo.BAJA) {
            throw new NegocioException("Un activo de baja no puede editarse; debe reactivarse primero.");
        }
        validar(a, a.getIdActivo());
        activoDAO.actualizar(a);
    }

    /**
     * Cambios de estado permitidos manualmente:
     * DISPONIBLE -> MANTENIMIENTO | BAJA ; MANTENIMIENTO -> DISPONIBLE | BAJA ; BAJA -> DISPONIBLE (reactivación).
     * ASIGNADO solo cambia mediante la devolución (AsignacionService).
     */
    public void cambiarEstado(int idActivo, EstadoActivo nuevo, int idUsuario, String detalle)
            throws NegocioException {
        Activo a = activoDAO.buscarPorId(idActivo);
        if (a == null) {
            throw new NegocioException("El activo no existe.");
        }
        EstadoActivo actual = a.getEstado();
        boolean permitido = switch (actual) {
            case DISPONIBLE -> nuevo == EstadoActivo.MANTENIMIENTO || nuevo == EstadoActivo.BAJA;
            case MANTENIMIENTO -> nuevo == EstadoActivo.DISPONIBLE || nuevo == EstadoActivo.BAJA;
            case BAJA -> nuevo == EstadoActivo.DISPONIBLE;
            case ASIGNADO -> false;
        };
        if (!permitido) {
            String msg = actual == EstadoActivo.ASIGNADO
                    ? "El activo está asignado: debe registrarse su devolución antes de cambiar su estado."
                    : "Cambio de estado no permitido: " + actual.getEtiqueta() + " → " + nuevo.getEtiqueta() + ".";
            throw new NegocioException(msg);
        }
        activoDAO.cambiarEstado(idActivo, nuevo, idUsuario, detalle);
    }

    /** Inventario completo en CSV (UTF-8, separado por comas) listo para abrir en Excel. */
    public String exportarCsv() {
        StringBuilder sb = new StringBuilder("Código,Nombre,Tipo,Estado,Responsable,Fecha de adquisición,Valor (S/)\r\n");
        for (ActivoDTO a : activoDAO.listar(null)) {
            sb.append(csv(a.getCodigo())).append(',')
              .append(csv(a.getNombre())).append(',')
              .append(csv(a.getTipo())).append(',')
              .append(csv(a.getEstado().getEtiqueta())).append(',')
              .append(csv(a.getResponsable())).append(',')
              .append(a.getFechaAdquisicion()).append(',')
              .append(a.getValor().toPlainString()).append("\r\n");
        }
        return sb.toString();
    }

    /** Escapa un campo CSV y neutraliza fórmulas (=, +, -, @) para evitar inyección en Excel. */
    static String csv(String valor) {
        if (valor == null) {
            return "";
        }
        String v = valor;
        if (!v.isEmpty() && "=+-@".indexOf(v.charAt(0)) >= 0) {
            v = "'" + v;
        }
        if (v.contains(",") || v.contains("\"") || v.contains("\n") || v.contains("\r")) {
            v = "\"" + v.replace("\"", "\"\"") + "\"";
        }
        return v;
    }

    private void validar(Activo a, int excluirId) throws NegocioException {
        if (a.getCodigo() == null || a.getCodigo().isBlank()) {
            throw new NegocioException("El código es obligatorio.");
        }
        if (a.getNombre() == null || a.getNombre().isBlank()) {
            throw new NegocioException("El nombre es obligatorio.");
        }
        if (a.getIdTipo() <= 0) {
            throw new NegocioException("Seleccione un tipo de activo.");
        }
        if (a.getFechaAdquisicion() == null || a.getFechaAdquisicion().isAfter(LocalDate.now())) {
            throw new NegocioException("La fecha de adquisición es obligatoria y no puede ser futura.");
        }
        if (a.getValor() == null || a.getValor().compareTo(BigDecimal.ZERO) < 0) {
            throw new NegocioException("El valor no puede ser negativo.");
        }
        if (activoDAO.existeCodigo(a.getCodigo().trim(), excluirId)) {
            throw new NegocioException("Ya existe un activo con el código " + a.getCodigo().trim() + ".");
        }
        a.setCodigo(a.getCodigo().trim());
        a.setNombre(a.getNombre().trim());
    }
}
