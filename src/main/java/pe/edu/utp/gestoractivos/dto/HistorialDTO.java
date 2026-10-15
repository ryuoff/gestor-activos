package pe.edu.utp.gestoractivos.dto;

import java.io.Serializable;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class HistorialDTO implements Serializable {
    private static final DateTimeFormatter FMT = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    private LocalDateTime fecha;
    private String estadoAnterior;
    private String estadoNuevo;
    private String usuario;
    private String detalle;
    private String codigoActivo;
    private String nombreActivo;

    public LocalDateTime getFecha() { return fecha; }
    public void setFecha(LocalDateTime fecha) { this.fecha = fecha; }
    public String getFechaTexto() { return fecha == null ? "" : fecha.format(FMT); }
    public String getEstadoAnterior() { return estadoAnterior; }
    public void setEstadoAnterior(String estadoAnterior) { this.estadoAnterior = estadoAnterior; }
    public String getEstadoNuevo() { return estadoNuevo; }
    public void setEstadoNuevo(String estadoNuevo) { this.estadoNuevo = estadoNuevo; }
    public String getUsuario() { return usuario; }
    public void setUsuario(String usuario) { this.usuario = usuario; }
    public String getDetalle() { return detalle; }
    public void setDetalle(String detalle) { this.detalle = detalle; }

    public String getCodigoActivo() { return codigoActivo; }
    public void setCodigoActivo(String codigoActivo) { this.codigoActivo = codigoActivo; }
    public String getNombreActivo() { return nombreActivo; }
    public void setNombreActivo(String nombreActivo) { this.nombreActivo = nombreActivo; }
}
