package pe.edu.utp.gestoractivos.util;

/** Se lanza cuando se viola una regla de negocio; el mensaje se muestra al usuario. */
public class NegocioException extends Exception {
    public NegocioException(String mensaje) { super(mensaje); }
}
