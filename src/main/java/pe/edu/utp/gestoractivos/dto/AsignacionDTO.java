package pe.edu.utp.gestoractivos.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class AsignacionDTO implements Serializable {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private int idAsignacion;
    private int idActivo;
    private String codigoActivo;
    private String nombreActivo;
    private String empleado;
    private String area;
    private LocalDateTime fechaAsignacion;
    private LocalDateTime fechaDevolucion;
    private String observacion;

    public int getIdAsignacion() { return idAsignacion; }
    public void setIdAsignacion(int idAsignacion) { this.idAsignacion = idAsignacion; }
    public int getIdActivo() { return idActivo; }
    public void setIdActivo(int idActivo) { this.idActivo = idActivo; }
    public String getCodigoActivo() { return codigoActivo; }
    public void setCodigoActivo(String codigoActivo) { this.codigoActivo = codigoActivo; }
    public String getNombreActivo() { return nombreActivo; }
    public void setNombreActivo(String nombreActivo) { this.nombreActivo = nombreActivo; }
    public String getEmpleado() { return empleado; }
    public void setEmpleado(String empleado) { this.empleado = empleado; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }
    public LocalDateTime getFechaAsignacion() { return fechaAsignacion; }
    public void setFechaAsignacion(LocalDateTime f) { this.fechaAsignacion = f; }
    public LocalDateTime getFechaDevolucion() { return fechaDevolucion; }
    public void setFechaDevolucion(LocalDateTime f) { this.fechaDevolucion = f; }
    public String getObservacion() { return observacion; }
    public void setObservacion(String observacion) { this.observacion = observacion; }

    public String getFechaAsignacionTexto() { return fechaAsignacion == null ? "" : fechaAsignacion.format(FMT); }
    public String getFechaDevolucionTexto() { return fechaDevolucion == null ? "—" : fechaDevolucion.format(FMT); }
    public boolean isVigente() { return fechaDevolucion == null; }

    public String getIniciales() { return ActivoDTO.iniciales(empleado); }
}
