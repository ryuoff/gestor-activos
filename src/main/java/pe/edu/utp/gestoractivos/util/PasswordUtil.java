package pe.edu.utp.gestoractivos.util;

import java.nio.charset.StandardCharsets;
import java.security.GeneralSecurityException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.util.HexFormat;
import javax.crypto.SecretKeyFactory;
import javax.crypto.spec.PBEKeySpec;

/**
 * Hash de contraseñas con PBKDF2-HMAC-SHA256 y sal aleatoria (solo clases del JDK).
 * Formato guardado en la BD:  pbkdf2$iteraciones$salHex$hashHex
 */
public final class PasswordUtil {
    private static final int ITERACIONES = 120_000;
    private static final int BITS = 256;
    private static final int BYTES_SAL = 16;
    private static final SecureRandom RANDOM = new SecureRandom();

    private PasswordUtil() { }

    public static String hash(String password) {
        byte[] sal = new byte[BYTES_SAL];
        RANDOM.nextBytes(sal);
        byte[] h = derivar(password, sal, ITERACIONES);
        HexFormat hex = HexFormat.of();
        return "pbkdf2$" + ITERACIONES + "$" + hex.formatHex(sal) + "$" + hex.formatHex(h);
    }

    /** Compara en tiempo constante la contraseña con el valor guardado. */
    public static boolean verificar(String password, String guardado) {
        if (password == null || guardado == null) {
            return false;
        }
        String[] p = guardado.split("\\$");
        if (p.length != 4 || !"pbkdf2".equals(p[0])) {
            return false;
        }
        try {
            HexFormat hex = HexFormat.of();
            byte[] esperado = hex.parseHex(p[3]);
            byte[] calculado = derivar(password, hex.parseHex(p[2]), Integer.parseInt(p[1]));
            return MessageDigest.isEqual(esperado, calculado);
        } catch (IllegalArgumentException e) {
            return false;
        }
    }

    private static byte[] derivar(String password, byte[] sal, int iteraciones) {
        try {
            PBEKeySpec spec = new PBEKeySpec(password.toCharArray(), sal, iteraciones, BITS);
            return SecretKeyFactory.getInstance("PBKDF2WithHmacSHA256").generateSecret(spec).getEncoded();
        } catch (GeneralSecurityException e) {
            throw new IllegalStateException("PBKDF2 no disponible", e);
        }
    }

    /** Token aleatorio (por ejemplo para CSRF) en hexadecimal. */
    public static String tokenAleatorio(int bytes) {
        byte[] b = new byte[bytes];
        RANDOM.nextBytes(b);
        return HexFormat.of().formatHex(b);
    }

    /** SHA-256 hexadecimal; se conserva solo para pruebas y compatibilidad. */
    public static String sha256(String texto) {
        try {
            MessageDigest md = MessageDigest.getInstance("SHA-256");
            return HexFormat.of().formatHex(md.digest(texto.getBytes(StandardCharsets.UTF_8)));
        } catch (NoSuchAlgorithmException e) {
            throw new IllegalStateException(e);
        }
    }
}
