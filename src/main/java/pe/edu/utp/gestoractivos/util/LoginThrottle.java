package pe.edu.utp.gestoractivos.util;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.util.concurrent.ConcurrentHashMap;

/** Bloquea temporalmente un usuario/IP tras varios intentos fallidos de inicio de sesión. */
public class LoginThrottle {
    private static final class Registro {
        int fallos;
        Instant bloqueadoHasta = Instant.MIN;
    }

    private final ConcurrentHashMap<String, Registro> registros = new ConcurrentHashMap<>();
    private final int maxIntentos;
    private final Duration bloqueo;
    private final Clock reloj;

    public LoginThrottle(int maxIntentos, Duration bloqueo, Clock reloj) {
        this.maxIntentos = maxIntentos;
        this.bloqueo = bloqueo;
        this.reloj = reloj;
    }

    public LoginThrottle() {
        this(5, Duration.ofMinutes(5), Clock.systemUTC());
    }

    public boolean bloqueado(String clave) {
        Registro r = registros.get(clave);
        return r != null && reloj.instant().isBefore(r.bloqueadoHasta);
    }

    public long segundosRestantes(String clave) {
        Registro r = registros.get(clave);
        if (r == null) {
            return 0;
        }
        return Math.max(0, Duration.between(reloj.instant(), r.bloqueadoHasta).toSeconds() + 1);
    }

    public void registrarFallo(String clave) {
        registros.compute(clave, (k, r) -> {
            Registro n = r == null ? new Registro() : r;
            if (!reloj.instant().isBefore(n.bloqueadoHasta)) {
                n.fallos++;
                if (n.fallos >= maxIntentos) {
                    n.bloqueadoHasta = reloj.instant().plus(bloqueo);
                    n.fallos = 0;
                }
            }
            return n;
        });
    }

    public void registrarExito(String clave) {
        registros.remove(clave);
    }
}
