package pe.edu.utp.gestoractivos.model;

/** Estados posibles de un activo (coincide con el ENUM de la BD). */
public enum EstadoActivo {
    DISPONIBLE("Disponible"),
    ASIGNADO("Asignado"),
    MANTENIMIENTO("En mantenimiento"),
    BAJA("De baja");

    private final String etiqueta;

    EstadoActivo(String etiqueta) { this.etiqueta = etiqueta; }

    public String getEtiqueta() { return etiqueta; }
}
