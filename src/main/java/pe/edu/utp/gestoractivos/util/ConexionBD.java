package pe.edu.utp.gestoractivos.util;

import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.sql.DataSource;

/**
 * PATRÓN SINGLETON: única instancia que entrega conexiones JDBC.
 * Si el servidor define el recurso JNDI jdbc/gestorActivos (por ejemplo, un pool en Payara),
 * lo usa; en caso contrario abre conexiones con db.properties (Tomcat / desarrollo).
 */
public final class ConexionBD {
    private static final Logger LOG = Logger.getLogger(ConexionBD.class.getName());
    private static final String[] JNDI = {"java:comp/env/jdbc/gestorActivos", "jdbc/gestorActivos"};
    private static final ConexionBD INSTANCIA = new ConexionBD();

    private final DataSource dataSource;   // puede ser null
    private final String url;
    private final String user;
    private final String password;

    private ConexionBD() {
        DataSource ds = buscarDataSource();
        this.dataSource = ds;

        Properties p = new Properties();
        try (InputStream in = ConexionBD.class.getClassLoader().getResourceAsStream("db.properties")) {
            if (in != null) {
                p.load(in);
            } else if (ds == null) {
                throw new IllegalStateException("No se encontró db.properties en el classpath");
            }
            if (ds == null) {
                Class.forName("com.mysql.cj.jdbc.Driver");
            }
        } catch (IOException | ClassNotFoundException e) {
            LOG.log(Level.SEVERE, "No se pudo inicializar la conexión a la BD", e);
            throw new IllegalStateException("No se pudo inicializar la conexión a la BD", e);
        }
        this.url = p.getProperty("db.url");
        this.user = p.getProperty("db.user");
        this.password = p.getProperty("db.password");
    }

    /**
     * Busca un pool JNDI y comprueba que funcione de verdad: algunos servidores (p. ej. Tomcat)
     * devuelven un DataSource vacío si el recurso está declarado pero no configurado.
     */
    private static DataSource buscarDataSource() {
        for (String nombre : JNDI) {
            try {
                DataSource ds = (DataSource) new InitialContext().lookup(nombre);
                try (Connection cn = ds.getConnection()) {
                    LOG.info("Usando DataSource JNDI " + nombre);
                    return ds;
                }
            } catch (NamingException | SQLException | RuntimeException e) {
                LOG.fine("DataSource " + nombre + " no disponible: " + e.getMessage());
            }
        }
        LOG.info("Sin DataSource JNDI utilizable; se usará db.properties");
        return null;
    }

    public static ConexionBD getInstancia() { return INSTANCIA; }

    public Connection getConnection() throws SQLException {
        return dataSource != null ? dataSource.getConnection() : DriverManager.getConnection(url, user, password);
    }
}
