package pe.edu.utp.gestoractivos.fake;

import java.util.List;
import pe.edu.utp.gestoractivos.dao.TipoActivoDAO;
import pe.edu.utp.gestoractivos.model.TipoActivo;

public class FakeTipoActivoDAO implements TipoActivoDAO {
    @Override
    public List<TipoActivo> listar() { return List.of(new TipoActivo(1, "Equipo"), new TipoActivo(2, "Otro")); }
}
