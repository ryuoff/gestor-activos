package pe.edu.utp.gestoractivos.model;

import java.io.Serializable;

public class TipoActivo implements Serializable {
    private int idTipo;
    private String nombre;

    public TipoActivo() { }
    public TipoActivo(int idTipo, String nombre) { this.idTipo = idTipo; this.nombre = nombre; }

    public int getIdTipo() { return idTipo; }
    public void setIdTipo(int idTipo) { this.idTipo = idTipo; }
    public String getNombre() { return nombre; }
    public void setNombre(String nombre) { this.nombre = nombre; }
}
