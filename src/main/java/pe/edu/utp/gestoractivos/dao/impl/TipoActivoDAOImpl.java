package pe.edu.utp.gestoractivos.dao.impl;

import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.List;
import pe.edu.utp.gestoractivos.dao.TipoActivoDAO;
import pe.edu.utp.gestoractivos.model.TipoActivo;
import pe.edu.utp.gestoractivos.util.ConexionBD;
import pe.edu.utp.gestoractivos.util.DaoException;

public class TipoActivoDAOImpl implements TipoActivoDAO {
    @Override
    public List<TipoActivo> listar() {
        List<TipoActivo> lista = new ArrayList<>();
        try (Connection cn = ConexionBD.getInstancia().getConnection();
             Statement st = cn.createStatement();
             ResultSet rs = st.executeQuery("SELECT id_tipo, nombre FROM tipo_activo ORDER BY nombre")) {
            while (rs.next()) {
                lista.add(new TipoActivo(rs.getInt("id_tipo"), rs.getString("nombre")));
            }
            return lista;
        } catch (SQLException e) {
            throw new DaoException("Error al listar tipos de activo", e);
        }
    }
}
