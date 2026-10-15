package pe.edu.utp.gestoractivos;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.math.BigDecimal;
import org.junit.jupiter.api.Test;
import pe.edu.utp.gestoractivos.dto.ResumenDTO;
import pe.edu.utp.gestoractivos.fake.FakeActivoDAO;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.service.DashboardService;

class DashboardServiceTest {
    @Test
    void cuentaPorEstadoYExcluyeLasBajasDelValor() {
        FakeActivoDAO dao = new FakeActivoDAO();
        dao.agregar("A1", EstadoActivo.DISPONIBLE);
        dao.agregar("A2", EstadoActivo.ASIGNADO);
        dao.agregar("A3", EstadoActivo.ASIGNADO);
        dao.agregar("A4", EstadoActivo.BAJA);
        ResumenDTO r = new DashboardService(dao).resumen();
        assertEquals(4, r.getTotal());
        assertEquals(2, r.getAsignados());
        assertEquals(0, r.getEnMantenimiento());
        assertEquals(1L, r.getPorEstado().get(EstadoActivo.BAJA));
        assertEquals(new BigDecimal("300.00"), r.getValorTotal());     // 3 activos en uso x 100
        assertEquals(4L, r.getPorTipo().get("Equipo"));
    }
}
