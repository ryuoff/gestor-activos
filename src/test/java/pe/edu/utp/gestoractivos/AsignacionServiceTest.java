package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.fake.FakeActivoDAO;
import pe.edu.utp.gestoractivos.fake.FakeAsignacionDAO;
import pe.edu.utp.gestoractivos.fake.FakeEmpleadoDAO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.service.AsignacionService;
import pe.edu.utp.gestoractivos.util.NegocioException;

class AsignacionServiceTest {
    private final FakeActivoDAO activos = new FakeActivoDAO();
    private final FakeEmpleadoDAO empleados = new FakeEmpleadoDAO();
    private final FakeAsignacionDAO asignaciones = new FakeAsignacionDAO(activos);
    private final AsignacionService service = new AsignacionService(asignaciones, activos, empleados);

    @Test
    void asignarPasaElActivoAAsignado() throws NegocioException {
        Activo a = activos.agregar("AC-1", EstadoActivo.DISPONIBLE);
        int emp = empleados.agregar("70000001", "a@x.pe", true).getIdEmpleado();
        service.asignar(a.getIdActivo(), emp, 1, "con cargador");
        assertEquals(EstadoActivo.ASIGNADO, a.getEstado());
        assertEquals(1, asignaciones.vigentes.size());
    }

    @Test
    void unActivoNoSePuedeAsignarDosVeces() throws NegocioException {
        Activo a = activos.agregar("AC-1", EstadoActivo.DISPONIBLE);
        int e1 = empleados.agregar("70000001", "a@x.pe", true).getIdEmpleado();
        int e2 = empleados.agregar("70000002", "b@x.pe", true).getIdEmpleado();
        service.asignar(a.getIdActivo(), e1, 1, null);
        assertThrows(NegocioException.class, () -> service.asignar(a.getIdActivo(), e2, 1, null));
        assertEquals(1, asignaciones.vigentes.size());
    }

    @Test
    void soloSeAsignanActivosDisponibles() {
        int emp = empleados.agregar("70000001", "a@x.pe", true).getIdEmpleado();
        for (EstadoActivo e : new EstadoActivo[]{EstadoActivo.BAJA, EstadoActivo.MANTENIMIENTO, EstadoActivo.ASIGNADO}) {
            Activo a = activos.agregar("AC-" + e, e);
            assertThrows(NegocioException.class, () -> service.asignar(a.getIdActivo(), emp, 1, null));
        }
    }

    @Test
    void noAsignaAEmpleadoInactivoOInexistente() {
        Activo a = activos.agregar("AC-1", EstadoActivo.DISPONIBLE);
        int inactivo = empleados.agregar("70000001", "a@x.pe", false).getIdEmpleado();
        assertThrows(NegocioException.class, () -> service.asignar(a.getIdActivo(), inactivo, 1, null));
        assertThrows(NegocioException.class, () -> service.asignar(a.getIdActivo(), 999, 1, null));
        assertEquals(EstadoActivo.DISPONIBLE, a.getEstado());
    }

    @Test
    void devolverDejaElActivoDisponibleYSoloUnaVez() throws NegocioException {
        Activo a = activos.agregar("AC-1", EstadoActivo.DISPONIBLE);
        int emp = empleados.agregar("70000001", "a@x.pe", true).getIdEmpleado();
        service.asignar(a.getIdActivo(), emp, 1, null);
        service.devolver(a.getIdActivo(), 1);
        assertEquals(EstadoActivo.DISPONIBLE, a.getEstado());
        assertThrows(NegocioException.class, () -> service.devolver(a.getIdActivo(), 1));
    }
}
