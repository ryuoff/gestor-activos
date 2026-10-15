package pe.edu.utp.gestoractivos.model;

import java.io.Serializable;
import java.math.BigDecimal;
import java.time.LocalDate;

public class Activo implements Serializable {
    private int idActivo;
    private String codigo;
    private String nombre;
    private String descripcion;
    private int idTipo;
    private EstadoActivo estado = EstadoActivo.DISPONIBLE;
    private LocalDate fechaAdquisicion;
    private BigDecimal valor = BigDecimal.ZERO;

    public int getIdActivo() { return idActivo; }
    public void setIdActivo(int idActivo) { this.idActivo = idActivo; }
    public String getCodigo() { return codigo; }
    public void setCodigo(String codigo) { this.codigo = codigo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
    public String getDescripcion() { return descripcion; }
    public void setDescripcion(String descripcion) { this.descripcion = descripcion; }
    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public EstadoActivo getEstado() { return estado; }
    public void setEstado(EstadoActivo estado) { this.estado = estado; }
    public LocalDate getFechaAdquisicion() { return fechaAdquisicion; }
    public void setFechaAdquisicion(LocalDate fechaAdquisicion) { this.fechaAdquisicion = fechaAdquisicion; }
    public BigDecimal getValor() { return valor; }
    public void setValor(BigDecimal valor) { this.valor = valor; }
}
