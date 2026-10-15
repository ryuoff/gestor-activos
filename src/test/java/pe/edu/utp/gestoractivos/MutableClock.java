package pe.edu.utp.gestoractivos;

import java.time.Clock;
import java.time.Duration;
import java.time.Instant;
import java.time.ZoneId;
import java.time.ZoneOffset;

/** Reloj controlable para probar bloqueos por tiempo. */
class MutableClock extends Clock {
    private Instant ahora = Instant.parse("2026-01-01T10:00:00Z");

    void avanzar(Duration d) { ahora = ahora.plus(d); }

    @Override
    public ZoneId getZone() { return ZoneOffset.UTC; }

    @Override
    public Clock withZone(ZoneId zone) { return this; }

    @Override
    public Instant instant() { return ahora; }
}
