package pe.edu.utp.gestoractivos.dao;

import java.util.List;
import pe.edu.utp.gestoractivos.model.Empleado;

public interface EmpleadoDAO {
    List<Empleado> listarActivos();
    List<Empleado> listarTodos();
    /** Empleados activos que todavía no tienen cuenta de acceso. */
    List<Empleado> listarSinUsuario();
    Empleado buscarPorId(int idEmpleado);
    boolean existeDni(String dni, int excluirId);
    boolean existeCorreo(String correo, int excluirId);
    int insertar(Empleado e);
    void actualizar(Empleado e);
    void cambiarActivo(int idEmpleado, boolean activo);
    int contarAsignacionesVigentes(int idEmpleado);
}
