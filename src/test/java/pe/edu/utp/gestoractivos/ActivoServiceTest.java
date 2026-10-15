package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.junit.jupiter.api.Assertions.assertTrue;

import java.math.BigDecimal;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.fake.FakeActivoDAO;
import pe.edu.utp.gestoractivos.fake.FakeTipoActivoDAO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.service.ActivoService;
import pe.edu.utp.gestoractivos.util.NegocioException;

class ActivoServiceTest {
    private final FakeActivoDAO dao = new FakeActivoDAO();
    private final ActivoService service = new ActivoService(dao, new FakeTipoActivoDAO());

    private Activo valido(String codigo) {
        Activo a = new Activo();
        a.setCodigo(codigo);
        a.setNombre("Laptop");
        a.setIdTipo(1);
        a.setFechaAdquisicion(LocalDate.now().minusDays(1));
        a.setValor(new BigDecimal("1500.00"));
        return a;
    }

    @Test
    void registraActivoValidoComoDisponible() throws NegocioException {
        int id = service.registrar(valido("AC-1"), 1);
        assertEquals(EstadoActivo.DISPONIBLE, dao.buscarPorId(id).getEstado());
    }

    @Test
    void rechazaCodigoDuplicado() throws NegocioException {
        service.registrar(valido("AC-1"), 1);
        assertThrows(NegocioException.class, () -> service.registrar(valido("AC-1"), 1));
    }

    @Test
    void rechazaCodigoVacioFechaFuturaYValorNegativo() {
        Activo sinCodigo = valido(" ");
        assertThrows(NegocioException.class, () -> service.registrar(sinCodigo, 1));
        Activo futuro = valido("AC-2");
        futuro.setFechaAdquisicion(LocalDate.now().plusDays(3));
        assertThrows(NegocioException.class, () -> service.registrar(futuro, 1));
        Activo negativo = valido("AC-3");
        negativo.setValor(new BigDecimal("-1"));
        assertThrows(NegocioException.class, () -> service.registrar(negativo, 1));
    }

    @Test
    void activoDeBajaNoSePuedeEditar() {
        Activo baja = dao.agregar("AC-9", EstadoActivo.BAJA);
        assertThrows(NegocioException.class, () -> service.editar(baja));
    }

    @Test
    void transicionesPermitidas() throws NegocioException {
        Activo a = dao.agregar("AC-1", EstadoActivo.DISPONIBLE);
        service.cambiarEstado(a.getIdActivo(), EstadoActivo.MANTENIMIENTO, 1, "falla");
        service.cambiarEstado(a.getIdActivo(), EstadoActivo.DISPONIBLE, 1, "listo");
        service.cambiarEstado(a.getIdActivo(), EstadoActivo.BAJA, 1, "obsoleto");
        service.cambiarEstado(a.getIdActivo(), EstadoActivo.DISPONIBLE, 1, "reactivación");
        assertEquals(EstadoActivo.DISPONIBLE, a.getEstado());
        assertEquals(4, dao.historial.size());
    }

    @Test
    void activoAsignadoNoCambiaDeEstadoSinDevolucion() {
        Activo a = dao.agregar("AC-1", EstadoActivo.ASIGNADO);
        assertThrows(NegocioException.class,
                () -> service.cambiarEstado(a.getIdActivo(), EstadoActivo.BAJA, 1, "x"));
        assertThrows(NegocioException.class,
                () -> service.cambiarEstado(a.getIdActivo(), EstadoActivo.MANTENIMIENTO, 1, "x"));
    }

    @Test
    void bajaNoPasaDirectoAMantenimiento() {
        Activo a = dao.agregar("AC-1", EstadoActivo.BAJA);
        assertThrows(NegocioException.class,
                () -> service.cambiarEstado(a.getIdActivo(), EstadoActivo.MANTENIMIENTO, 1, "x"));
    }

    @Test
    void csvEscapaComasComillasYFormulas() {
        Activo a = dao.agregar("AC-1", EstadoActivo.DISPONIBLE);
        a.setNombre("Monitor 24\", HDMI");
        Activo b = dao.agregar("=CMD()", EstadoActivo.DISPONIBLE);
        b.setNombre("Normal");
        String csv = service.exportarCsv();
        assertTrue(csv.startsWith("Código,Nombre,Tipo"));
        assertTrue(csv.contains("\"Monitor 24\"\", HDMI\""));
        assertTrue(csv.contains("'=CMD()"));
    }
}
