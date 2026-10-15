# Despliegue en Payara Server 6

La guía del curso pide un servidor de aplicaciones distinto de Tomcat para el proyecto final. Este es el procedimiento para Payara. La aplicación usa solo APIs estándar de Jakarta EE 10, así que el mismo WAR sirve para Tomcat y para Payara.

> Nota: esta guía no se pudo probar en el entorno donde se preparó el proyecto. Si un paso falla, revisa el log del servidor (`glassfish/domains/domain1/logs/server.log`).

## 1. Instalar Payara y registrarlo en NetBeans
1. Descarga **Payara Server 6 (Community, Full)** desde https://www.payara.fish/downloads/ y descomprímelo (por ejemplo en `C:\payara6`).
2. NetBeans → pestaña *Services* → clic derecho en *Servers* → *Add Server…* → **GlassFish Server** (sirve para Payara) → elige la carpeta `C:\payara6\glassfish`.
3. Verifica que el servidor use JDK 21 (*Tools → Java Platforms*).

## 2. Crear el pool de conexiones a MySQL
1. Copia el driver `mysql-connector-j-8.4.0.jar` a `C:\payara6\glassfish\domains\domain1\lib` (lo encuentras en tu repositorio local de Maven: `~/.m2/repository/com/mysql/mysql-connector-j/8.4.0/`).
2. Inicia el servidor y abre una terminal en `C:\payara6\glassfish\bin`:

```bash
asadmin create-jdbc-connection-pool --datasourceclassname com.mysql.cj.jdbc.MysqlDataSource --restype javax.sql.DataSource --property user=root:password=root:serverName=localhost:portNumber=3306:databaseName=gestor_activos:useSSL=false:allowPublicKeyRetrieval=true:serverTimezone=America/Lima gestorPool

asadmin create-jdbc-resource --connectionpoolid gestorPool jdbc/gestorActivos

asadmin ping-connection-pool gestorPool
```
Cambia `user` y `password` por los de tu MySQL. El último comando debe responder "Command ping-connection-pool executed successfully".

También puedes hacerlo desde la consola web: http://localhost:4848 → *Resources → JDBC*.

## 3. Desplegar
1. En NetBeans: clic derecho en el proyecto → *Properties → Run* → *Server*: Payara/GlassFish → **Run**.
2. La aplicación detecta el recurso `jdbc/gestorActivos` y lo usa; en el log verás `Usando DataSource JNDI java:comp/env/jdbc/gestorActivos`. Si no existe, vuelve a usar `db.properties` (como en Tomcat).
3. Abre `http://localhost:8080/gestor-activos/` (en Payara el contexto lleva el nombre del WAR).

## Por qué Payara
Incluye el perfil completo de Jakarta EE 10 (JSF, JAX-RS, CDI, JPA, EJB), que se usará en los siguientes avances, tiene consola de administración web y NetBeans lo administra directamente.
