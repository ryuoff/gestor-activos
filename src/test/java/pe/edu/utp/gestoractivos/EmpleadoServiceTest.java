package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.fake.FakeEmpleadoDAO;
import pe.edu.utp.gestoractivos.model.Empleado;
import pe.edu.utp.gestoractivos.service.EmpleadoService;
import pe.edu.utp.gestoractivos.util.NegocioException;

class EmpleadoServiceTest {
    private final FakeEmpleadoDAO dao = new FakeEmpleadoDAO();
    private final EmpleadoService service = new EmpleadoService(dao);

    private Empleado nuevo(String dni, String correo) {
        Empleado e = new Empleado();
        e.setDni(dni);
        e.setNombres("Ana");
        e.setApellidos("Díaz");
        e.setCorreo(correo);
        e.setArea("Contabilidad");
        return e;
    }

    @Test
    void registraEmpleadoValidoYNormalizaElCorreo() throws NegocioException {
        Empleado e = nuevo("12345678", " ANA@Empresa.PE ");
        service.registrar(e);
        assertEquals("ana@empresa.pe", e.getCorreo());
        assertEquals(1, dao.empleados.size());
    }

    @Test
    void rechazaDniYCorreoInvalidos() {
        assertThrows(NegocioException.class, () -> service.registrar(nuevo("1234", "a@x.pe")));
        assertThrows(NegocioException.class, () -> service.registrar(nuevo("12345678", "sin-arroba")));
    }

    @Test
    void rechazaDniOCorreoDuplicados() throws NegocioException {
        service.registrar(nuevo("12345678", "a@x.pe"));
        assertThrows(NegocioException.class, () -> service.registrar(nuevo("12345678", "otro@x.pe")));
        assertThrows(NegocioException.class, () -> service.registrar(nuevo("87654321", "a@x.pe")));
    }

    @Test
    void editarPermiteConservarSuPropioDni() throws NegocioException {
        int id = service.registrar(nuevo("12345678", "a@x.pe"));
        Empleado cambio = nuevo("12345678", "a@x.pe");
        cambio.setIdEmpleado(id);
        cambio.setArea("Logística");
        service.editar(cambio);
        assertEquals("Logística", dao.buscarPorId(id).getArea());
    }

    @Test
    void noSeDesactivaConActivosAsignados() throws NegocioException {
        Empleado e = dao.agregar("11111111", "a@x.pe", true);
        dao.asignacionesVigentes.put(e.getIdEmpleado(), 2);
        assertThrows(NegocioException.class, () -> service.cambiarActivo(e.getIdEmpleado(), false));
        assertTrue(e.isActivo());
        dao.asignacionesVigentes.put(e.getIdEmpleado(), 0);
        service.cambiarActivo(e.getIdEmpleado(), false);
        assertFalse(e.isActivo());
    }
}
