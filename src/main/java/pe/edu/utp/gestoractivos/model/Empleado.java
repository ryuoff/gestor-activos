package pe.edu.utp.gestoractivos.model;

import java.io.Serializable;

public class Empleado implements Serializable {
    private int idEmpleado;
    private String dni;
    private String nombres;
    private String apellidos;
    private String correo;
    private String area;
    private boolean activo = true;

    public int getIdEmpleado() { return idEmpleado; }
    public void setIdEmpleado(int idEmpleado) { this.idEmpleado = idEmpleado; }
    public String getDni() { return dni; }
    public void setDni(String dni) { this.dni = dni; }
    public String getNombres() { return nombres; }
    public void setNombres(String nombres) { this.nombres = nombres; }
    public String getApellidos() { return apellidos; }
    public void setApellidos(String apellidos) { this.apellidos = apellidos; }
    public String getCorreo() { return correo; }
    public void setCorreo(String correo) { this.correo = correo; }
    public String getArea() { return area; }
    public void setArea(String area) { this.area = area; }

    public boolean isActivo() { return activo; }
    public void setActivo(boolean activo) { this.activo = activo; }

    public String getNombreCompleto() { return nombres + " " + apellidos; }
}
