package pe.edu.utp.gestoractivos.service;

import java.util.List;
import java.util.regex.Pattern;
import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dao.EmpleadoDAO;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.util.NegocioException;

public class EmpleadoService {
    private static final Pattern DNI = Pattern.compile("\\d{8}");
    private static final Pattern CORREO = Pattern.compile("[^@\\s]+@[^@\\s]+\\.[^@\\s]+");

    private final EmpleadoDAO dao;

    public EmpleadoService() {
        this(DAOFactory.getEmpleadoDAO());
    }

    public EmpleadoService(EmpleadoDAO dao) {
        this.dao = dao;
    }

    public List<Empleado> listar() { return dao.listarTodos(); }
    public Empleado buscar(int id) { return dao.buscarPorId(id); }

    public int registrar(Empleado e) throws NegocioException {
        validar(e, 0);
        return dao.insertar(e);
    }

    public void editar(Empleado e) throws NegocioException {
        if (dao.buscarPorId(e.getIdEmpleado()) == null) {
            throw new NegocioException("El empleado no existe.");
        }
        validar(e, e.getIdEmpleado());
        dao.actualizar(e);
    }

    /** Un empleado con activos asignados no puede desactivarse hasta devolverlos. */
    public void cambiarActivo(int id, boolean activo) throws NegocioException {
        Empleado e = dao.buscarPorId(id);
        if (e == null) {
            throw new NegocioException("El empleado no existe.");
        }
        if (!activo && dao.contarAsignacionesVigentes(id) > 0) {
            throw new NegocioException("No se puede desactivar: tiene activos asignados. Registra primero su devolución.");
        }
        dao.cambiarActivo(id, activo);
    }

    private void validar(Empleado e, int excluirId) throws NegocioException {
        e.setDni(limpiar(e.getDni()));
        e.setNombres(limpiar(e.getNombres()));
        e.setApellidos(limpiar(e.getApellidos()));
        e.setCorreo(limpiar(e.getCorreo()).toLowerCase());
        e.setArea(limpiar(e.getArea()));
        if (!DNI.matcher(e.getDni()).matches()) {
            throw new NegocioException("El DNI debe tener 8 dígitos.");
        }
        if (e.getNombres().isEmpty() || e.getApellidos().isEmpty()) {
            throw new NegocioException("Nombres y apellidos son obligatorios.");
        }
        if (!CORREO.matcher(e.getCorreo()).matches()) {
            throw new NegocioException("Ingresa un correo válido (por ejemplo, nombre@empresa.pe).");
        }
        if (e.getArea().isEmpty()) {
            throw new NegocioException("El área es obligatoria.");
        }
        if (dao.existeDni(e.getDni(), excluirId)) {
            throw new NegocioException("Ya existe un empleado con el DNI " + e.getDni() + ".");
        }
        if (dao.existeCorreo(e.getCorreo(), excluirId)) {
            throw new NegocioException("Ya existe un empleado con el correo " + e.getCorreo() + ".");
        }
    }

    private static String limpiar(String s) {
        return s == null ? "" : s.trim();
    }
}
