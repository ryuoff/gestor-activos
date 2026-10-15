package pe.edu.utp.gestoractivos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

/** Datos del panel de inicio. */
public class ResumenDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int total;
    private BigDecimal valorTotal = BigDecimal.ZERO;
    private Map<EstadoActivo, Long> porEstado;
    private Map<String, Long> porTipo;
    private List<HistorialDTO> movimientos;

    public int getTotal() { return total; }
    public void setTotal(int total) { this.total = total; }
    public BigDecimal getValorTotal() { return valorTotal; }
    public void setValorTotal(BigDecimal valorTotal) { this.valorTotal = valorTotal; }
    public Map<EstadoActivo, Long> getPorEstado() { return porEstado; }
    public void setPorEstado(Map<EstadoActivo, Long> porEstado) { this.porEstado = porEstado; }
    public Map<String, Long> getPorTipo() { return porTipo; }
    public void setPorTipo(Map<String, Long> porTipo) { this.porTipo = porTipo; }
    public List<HistorialDTO> getMovimientos() { return movimientos; }
    public void setMovimientos(List<HistorialDTO> movimientos) { this.movimientos = movimientos; }

    /** Atajos para las vistas (EL no puede indexar un EnumMap con texto). */
    public long getAsignados() { return porEstado == null ? 0 : porEstado.getOrDefault(EstadoActivo.ASIGNADO, 0L); }
    public long getEnMantenimiento() { return porEstado == null ? 0 : porEstado.getOrDefault(EstadoActivo.MANTENIMIENTO, 0L); }
}
