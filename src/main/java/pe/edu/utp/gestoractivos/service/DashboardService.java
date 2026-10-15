package pe.edu.utp.gestoractivos.service;

import java.math.BigDecimal;
import java.util.EnumMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.dao.ActivoDAO;
import pe.edu.utp.gestoractivos.dao.DAOFactory;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.dto.ResumenDTO;
import pe.edu.utp.gestoractivos.model.EstadoActivo;

/** Calcula los indicadores del panel de inicio. */
public class DashboardService {
    private final ActivoDAO activoDAO;

    public DashboardService() {
        this(DAOFactory.getActivoDAO());
    }

    public DashboardService(ActivoDAO activoDAO) {
        this.activoDAO = activoDAO;
    }

    public ResumenDTO resumen() {
        List<ActivoDTO> activos = activoDAO.listar(null);
        Map<EstadoActivo, Long> porEstado = new EnumMap<>(EstadoActivo.class);
        for (EstadoActivo e : EstadoActivo.values()) {
            porEstado.put(e, 0L);
        }
        Map<String, Long> porTipo = new LinkedHashMap<>();
        BigDecimal valor = BigDecimal.ZERO;
        for (ActivoDTO a : activos) {
            porEstado.merge(a.getEstado(), 1L, Long::sum);
            porTipo.merge(a.getTipo(), 1L, Long::sum);
            if (a.getEstado() != EstadoActivo.BAJA) {          // los dados de baja ya no cuentan como patrimonio
                valor = valor.add(a.getValor());
            }
        }
        ResumenDTO r = new ResumenDTO();
        r.setTotal(activos.size());
        r.setValorTotal(valor);
        r.setPorEstado(porEstado);
        r.setPorTipo(porTipo);
        r.setMovimientos(activoDAO.movimientosRecientes(8));
        return r;
    }
}
