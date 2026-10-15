package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertFalse;
import static org.junit.jupiter.api.Assertions.assertNotEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.util.PasswordUtil;

class PasswordUtilTest {
    @Test
    void verificaLaClaveCorrectaYRechazaLaIncorrecta() {
        String h = PasswordUtil.hash("Secreta123");
        assertTrue(PasswordUtil.verificar("Secreta123", h));
        assertFalse(PasswordUtil.verificar("secreta123", h));
    }

    @Test
    void cadaHashUsaUnaSalDistinta() {
        assertNotEquals(PasswordUtil.hash("igual"), PasswordUtil.hash("igual"));
    }

    @Test
    void formatoInvalidoNuncaVerifica() {
        assertFalse(PasswordUtil.verificar("x", null));
        assertFalse(PasswordUtil.verificar("x", "no-es-un-hash"));
        assertFalse(PasswordUtil.verificar("x", "pbkdf2$abc$zz$zz"));
        assertFalse(PasswordUtil.verificar("x", PasswordUtil.sha256("x")));
    }

    @Test
    void hashSemillaDeLaBaseDeDatosEsValido() {
        String admin = "pbkdf2$120000$ee6a6760a800dc0bdf7d67dc07f1c216$eb920da113e0c923ab4960a6bf7a09e8600b7ff4a7379531c27736dc02b4949e";
        assertTrue(PasswordUtil.verificar("admin123", admin));
        assertEquals(64, PasswordUtil.sha256("x").length());
        assertEquals(48, PasswordUtil.tokenAleatorio(24).length());
    }
}
