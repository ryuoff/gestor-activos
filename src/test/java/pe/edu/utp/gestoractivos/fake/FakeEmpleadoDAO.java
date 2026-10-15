package pe.edu.utp.gestoractivos.fake;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.dao.EmpleadoDAO;
import pe.edu.utp.gestoractivos.model.Empleado;

public class FakeEmpleadoDAO implements EmpleadoDAO {
    public final Map<Integer, Empleado> empleados = new LinkedHashMap<>();
    public final Map<Integer, Integer> asignacionesVigentes = new LinkedHashMap<>();
    public final List<Integer> conUsuario = new ArrayList<>();
    private int sig = 1;

    public Empleado agregar(String dni, String correo, boolean activo) {
        Empleado e = new Empleado();
        e.setIdEmpleado(sig++);
        e.setDni(dni);
        e.setNombres("Nombre" + e.getIdEmpleado());
        e.setApellidos("Apellido");
        e.setCorreo(correo);
        e.setArea("Sistemas");
        e.setActivo(activo);
        empleados.put(e.getIdEmpleado(), e);
        return e;
    }

    @Override
    public List<Empleado> listarActivos() { return empleados.values().stream().filter(Empleado::isActivo).toList(); }
    @Override
    public List<Empleado> listarTodos() { return new ArrayList<>(empleados.values()); }
    @Override
    public List<Empleado> listarSinUsuario() {
        return empleados.values().stream().filter(e -> e.isActivo() && !conUsuario.contains(e.getIdEmpleado())).toList();
    }
    @Override
    public Empleado buscarPorId(int id) { return empleados.get(id); }
    @Override
    public boolean existeDni(String dni, int excluirId) {
        return empleados.values().stream().anyMatch(e -> e.getDni().equals(dni) && e.getIdEmpleado() != excluirId);
    }
    @Override
    public boolean existeCorreo(String correo, int excluirId) {
        return empleados.values().stream().anyMatch(e -> e.getCorreo().equals(correo) && e.getIdEmpleado() != excluirId);
    }
    @Override
    public int insertar(Empleado e) {
        e.setIdEmpleado(sig++);
        empleados.put(e.getIdEmpleado(), e);
        return e.getIdEmpleado();
    }
    @Override
    public void actualizar(Empleado e) { empleados.put(e.getIdEmpleado(), e); }
    @Override
    public void cambiarActivo(int id, boolean activo) { empleados.get(id).setActivo(activo); }
    @Override
    public int contarAsignacionesVigentes(int id) { return asignacionesVigentes.getOrDefault(id, 0); }
}
