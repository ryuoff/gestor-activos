package pe.edu.utp.gestoractivos.model;

import java.io.Serializable;

public class Usuario implements Serializable {
    private int idUsuario;
    private String username;
    private String passwordHash;
    private String rol;          // ADMINISTRADOR | EMPLEADO
    private Integer idEmpleado;
    private String nombreCompleto;
    private boolean activo = true;

    public int getIdUsuario() { return idUsuario; }
    public void setIdUsuario(int idUsuario) { this.idUsuario = idUsuario; }
    public String getUsername() { return username; }
    public void setUsername(String username) { this.username = username; }
    public String getPasswordHash() { return passwordHash; }
    public void setPasswordHash(String passwordHash) { this.passwordHash = passwordHash; }
    public String getRol() { return rol; }
    public void setRol(String rol) { this.rol = rol; }
    public Integer getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(Integer idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getNombreCompleto() { return nombreCompleto; }
    public void setNombreCompleto(String nombreCompleto) { this.nombreCompleto = nombreCompleto; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public boolean isAdministrador() { return "ADMINISTRADOR".equals(rol); }
}
