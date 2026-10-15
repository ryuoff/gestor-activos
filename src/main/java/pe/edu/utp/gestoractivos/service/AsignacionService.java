package pe.edu.utp.gestoractivos.service;

import java.util.List;
import pe.edu.utp.gestoractivos.dao.ActivoDAO;
import pe.edu.utp.gestoractivos.dao.AsignacionDAO;
import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dao.EmpleadoDAO;
import pe.edu.utp.gestoractivos.dto.AsignacionDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.util.NegocioException;

public class AsignacionService {
    private final AsignacionDAO asignacionDAO;
    private final ActivoDAO activoDAO;
    private final EmpleadoDAO empleadoDAO;

    public AsignacionService() {
        this(DAOFactory.getAsignacionDAO(), DAOFactory.getActivoDAO(), DAOFactory.getEmpleadoDAO());
    }

    /** Constructor para pruebas: permite inyectar DAO falsos. */
    public AsignacionService(AsignacionDAO asignacionDAO, ActivoDAO activoDAO, EmpleadoDAO empleadoDAO) {
        this.asignacionDAO = asignacionDAO;
        this.activoDAO = activoDAO;
        this.empleadoDAO = empleadoDAO;
    }

    public List<AsignacionDTO> vigentes() { return asignacionDAO.listarVigentes(); }
    public List<AsignacionDTO> porEmpleado(int idEmpleado) { return asignacionDAO.listarPorEmpleado(idEmpleado); }
    public List<Empleado> empleados() { return empleadoDAO.listarActivos(); }

    /** Regla: solo se asigna un activo DISPONIBLE, y a un único empleado a la vez. */
    public void asignar(int idActivo, int idEmpleado, int idUsuario, String observacion) throws NegocioException {
        Activo a = activoDAO.buscarPorId(idActivo);
        if (a == null) {
            throw new NegocioException("El activo no existe.");
        }
        if (a.getEstado() != EstadoActivo.DISPONIBLE) {
            throw new NegocioException("Solo pueden asignarse activos disponibles (estado actual: "
                    + a.getEstado().getEtiqueta() + ").");
        }
        Empleado emp = empleadoDAO.buscarPorId(idEmpleado);
        if (emp == null) {
            throw new NegocioException("El empleado seleccionado no existe.");
        }
        if (!emp.isActivo()) {
            throw new NegocioException("El empleado está desactivado y no puede recibir activos.");
        }
        if (!asignacionDAO.asignar(idActivo, idEmpleado, idUsuario, observacion)) {
            throw new NegocioException("El activo dejó de estar disponible; actualice la lista.");
        }
    }

    public void devolver(int idActivo, int idUsuario) throws NegocioException {
        if (!asignacionDAO.devolver(idActivo, idUsuario)) {
            throw new NegocioException("El activo no tiene una asignación vigente.");
        }
    }
}
