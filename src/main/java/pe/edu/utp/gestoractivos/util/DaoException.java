package pe.edu.utp.gestoractivos.util;

import java.util.logging.Level;
import java.util.logging.Logger;

/** Excepción no verificada que envuelve los errores SQL de la capa DAO (queda registrada en el log). */
public class DaoException extends RuntimeException {
    private static final long serialVersionUID = 1L;

    public DaoException(String mensaje, Throwable causa) {
        super(mensaje, causa);
        Logger.getLogger(DaoException.class.getName()).log(Level.SEVERE, mensaje, causa);
    }
}
