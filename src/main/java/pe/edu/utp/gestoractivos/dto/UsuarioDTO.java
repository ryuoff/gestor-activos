package pe.edu.utp.gestoractivos.dto;

import java.io.Serializable;

public class UsuarioDTO implements Serializable {
    private static final long serialVersionUID = 1L;

    private int idUsuario;
    private String username;
    private String rol;
    private String empleado;     // nombre del empleado vinculado (o null)
    private boolean activo;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public String getEmpleado() { return empleado; }
    public void setEmpleado(String empleado) { this.empleado = empleado; }
    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }
    public boolean isAdministrador() { return "ADMINISTRADOR".equals(rol); }
    public String getIniciales() { return ActivoDTO.iniciales(empleado != null ? empleado : username); }
}
