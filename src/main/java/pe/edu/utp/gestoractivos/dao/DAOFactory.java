package pe.edu.utp.gestoractivos.dao;

import pe.edu.utp.gestoractivos.dao.impl.ActivoDAOImpl;
import pe.edu.utp.gestoractivos.dao.impl.AsignacionDAOImpl;
import pe.edu.utp.gestoractivos.dao.impl.EmpleadoDAOImpl;
import pe.edu.utp.gestoractivos.dao.impl.TipoActivoDAOImpl;
import pe.edu.utp.gestoractivos.dao.impl.UsuarioDAOImpl;

/**
 * PATRÓN FACTORY: centraliza la creación de los DAO. Las capas superiores dependen
 * solo de las interfaces; cambiar de MySQL a otro motor implica tocar únicamente esta clase.
 */
public final class DAOFactory {
    private DAOFactory() { }

    public static UsuarioDAO getUsuarioDAO() { return new UsuarioDAOImpl(); }
    public static EmpleadoDAO getEmpleadoDAO() { return new EmpleadoDAOImpl(); }
    public static TipoActivoDAO getTipoActivoDAO() { return new TipoActivoDAOImpl(); }
    public static ActivoDAO getActivoDAO() { return new ActivoDAOImpl(); }
    public static AsignacionDAO getAsignacionDAO() { return new AsignacionDAOImpl(); }
}
