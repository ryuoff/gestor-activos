package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertNull;
import static org.junit.jupiter.api.Assertions.assertThrows;

import java.time.Duration;
import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.fake.FakeUsuarioDAO;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.AuthService;
import pe.edu.utp.gestoractivos.util.LoginThrottle;
import pe.edu.utp.gestoractivos.util.NegocioException;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

class AuthServiceTest {
    private final FakeUsuarioDAO dao = new FakeUsuarioDAO();
    private final MutableClock reloj = new MutableClock();
    private final AuthService auth = new AuthService(dao, new LoginThrottle(3, Duration.ofMinutes(5), reloj));

    AuthServiceTest() {
        dao.agregar("admin", PasswordUtil.hash("admin123"), "ADMINISTRADOR", null);
    }

    @Test
    void credencialesCorrectasDevuelvenElUsuario() throws NegocioException {
        Usuario u = auth.autenticar("admin", "admin123", "1.1.1.1");
        assertNotNull(u);
        assertEquals("ADMINISTRADOR", u.getRol());
    }

    @Test
    void claveIncorrectaOUsuarioInexistenteDevuelvenNull() throws NegocioException {
        assertNull(auth.autenticar("admin", "otra", "1.1.1.1"));
        assertNull(auth.autenticar("nadie", "admin123", "1.1.1.1"));
        assertNull(auth.autenticar("", "", "1.1.1.1"));
    }

    @Test
    void usuarioDesactivadoNoPuedeIngresar() throws NegocioException {
        dao.buscarPorUsername("admin").setActivo(false);
        assertNull(auth.autenticar("admin", "admin123", "1.1.1.1"));
    }

    @Test
    void tresFallosBloqueanYLuegoSeLibera() throws NegocioException {
        for (int i = 0; i < 3; i++) {
            assertNull(auth.autenticar("admin", "mal", "9.9.9.9"));
        }
        assertThrows(NegocioException.class, () -> auth.autenticar("admin", "admin123", "9.9.9.9"));
        reloj.avanzar(Duration.ofMinutes(6));
        assertNotNull(auth.autenticar("admin", "admin123", "9.9.9.9"));
    }

    @Test
    void elBloqueoEsPorUsuarioEIp() throws NegocioException {
        for (int i = 0; i < 3; i++) {
            auth.autenticar("admin", "mal", "9.9.9.9");
        }
        assertNotNull(auth.autenticar("admin", "admin123", "8.8.8.8"));
    }

    @Test
    void unAccesoCorrectoReiniciaElContador() throws NegocioException {
        auth.autenticar("admin", "mal", "7.7.7.7");
        auth.autenticar("admin", "mal", "7.7.7.7");
        assertNotNull(auth.autenticar("admin", "admin123", "7.7.7.7"));
        auth.autenticar("admin", "mal", "7.7.7.7");
        auth.autenticar("admin", "mal", "7.7.7.7");
        assertNotNull(auth.autenticar("admin", "admin123", "7.7.7.7"));
    }
}
