package pe.edu.utp.gestoractivos.dto;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

/** Objeto de transferencia para listar activos con datos de otras tablas (tipo y responsable). */
public class ActivoDTO implements Serializable {
    private int idActivo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private String tipo;
    private EstadoActivo estado;
    private LocalDate fechaAdquisicion;
    private BigDecimal valor;
    private String responsable;   // empleado con asignación vigente (o null)

    public int getIdActivo() { return idActivo; }
    public void setIdActivo(int idActivo) { this.idActivo = idActivo; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public String getTipo() { return tipo; }
    public void setTipo(String tipo) { this.tipo = tipo; }
    public EstadoActivo getEstado() { return estado; }
    public void setEstado(EstadoActivo estado) { this.estado = estado; }
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
    public String getResponsable() { return responsable; }
    public void setResponsable(String responsable) { this.responsable = responsable; }

    /** Iniciales (máx. 2) de un nombre completo, para el avatar. */
    public static String iniciales(String nombre) {
        if (nombre == null || nombre.isBlank()) { return ""; }
        String[] p = nombre.trim().split("\\s+");
        String r = p[0].substring(0, 1);
        if (p.length > 1) { r += p[p.length - 1].substring(0, 1); }
        return r.toUpperCase();
    }

    public String getInicialesResponsable() { return iniciales(responsable); }
}
