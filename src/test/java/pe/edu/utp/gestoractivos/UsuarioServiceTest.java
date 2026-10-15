package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.fake.FakeEmpleadoDAO;
import pe.edu.utp.gestoractivos.fake.FakeUsuarioDAO;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.UsuarioService;
import pe.edu.utp.gestoractivos.util.NegocioException;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

class UsuarioServiceTest {
    private final FakeUsuarioDAO usuarios = new FakeUsuarioDAO();
    private final FakeEmpleadoDAO empleados = new FakeEmpleadoDAO();
    private final UsuarioService service = new UsuarioService(usuarios, empleados);

    @Test
    void creaUsuarioConContrasenaCifrada() throws NegocioException {
        int emp = empleados.agregar("11111111", "a@x.pe", true).getIdEmpleado();
        int id = service.crear("jperez", "Clave1234", "EMPLEADO", emp);
        String hash = usuarios.buscarPorId(id).getPasswordHash();
        assertTrue(hash.startsWith("pbkdf2$"));
        assertTrue(PasswordUtil.verificar("Clave1234", hash));
    }

    @Test
    void validaUsernameYContrasena() {
        assertThrows(NegocioException.class, () -> service.crear("a b", "Clave1234", "ADMINISTRADOR", null));
        assertThrows(NegocioException.class, () -> service.crear("admin2", "corta1", "ADMINISTRADOR", null));
        assertThrows(NegocioException.class, () -> service.crear("admin2", "sololetras", "ADMINISTRADOR", null));
        assertThrows(NegocioException.class, () -> service.crear("admin2", "Clave1234", "SUPERUSUARIO", null));
    }

    @Test
    void rolEmpleadoRequiereEmpleadoActivoSinCuenta() throws NegocioException {
        assertThrows(NegocioException.class, () -> service.crear("emp1", "Clave1234", "EMPLEADO", null));
        int inactivo = empleados.agregar("11111111", "a@x.pe", false).getIdEmpleado();
        assertThrows(NegocioException.class, () -> service.crear("emp1", "Clave1234", "EMPLEADO", inactivo));
        int activo = empleados.agregar("22222222", "b@x.pe", true).getIdEmpleado();
        service.crear("emp1", "Clave1234", "EMPLEADO", activo);
        assertThrows(NegocioException.class, () -> service.crear("emp2", "Clave1234", "EMPLEADO", activo));
    }

    @Test
    void usernameDuplicadoSeRechaza() throws NegocioException {
        service.crear("admin2", "Clave1234", "ADMINISTRADOR", null);
        assertThrows(NegocioException.class, () -> service.crear("admin2", "Clave1234", "ADMINISTRADOR", null));
    }

    @Test
    void noSePuedeDesactivarLaPropiaCuentaNiAlUltimoAdministrador() throws NegocioException {
        Usuario a1 = usuarios.agregar("admin1", "x", "ADMINISTRADOR", null);
        Usuario a2 = usuarios.agregar("admin2", "x", "ADMINISTRADOR", null);
        assertThrows(NegocioException.class, () -> service.cambiarActivo(a1.getIdUsuario(), false, a1.getIdUsuario()));
        service.cambiarActivo(a2.getIdUsuario(), false, a1.getIdUsuario());
        assertFalse(a2.isActivo());
        // a1 es ahora el único administrador activo: otro usuario no puede desactivarlo
        Usuario emp = usuarios.agregar("emp", "x", "EMPLEADO", 5);
        assertThrows(NegocioException.class, () -> service.cambiarActivo(a1.getIdUsuario(), false, emp.getIdUsuario()));
        assertTrue(a1.isActivo());
    }

    @Test
    void cambiarContrasenaGuardaUnHashNuevoYValida() throws NegocioException {
        Usuario u = usuarios.agregar("admin1", PasswordUtil.hash("Vieja1234"), "ADMINISTRADOR", null);
        assertThrows(NegocioException.class, () -> service.cambiarPassword(u.getIdUsuario(), "corta"));
        service.cambiarPassword(u.getIdUsuario(), "Nueva1234");
        assertTrue(PasswordUtil.verificar("Nueva1234", u.getPasswordHash()));
        assertFalse(PasswordUtil.verificar("Vieja1234", u.getPasswordHash()));
        assertEquals(1, usuarios.usuarios.size());
    }
}
