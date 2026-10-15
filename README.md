# Gestor de Activos Empresariales — Avance 2

Sistema web (Jakarta EE 10 · JDK 21 · MySQL 8) para registrar, asignar y consultar activos de una empresa.

## Funcionalidades
| Módulo | Qué permite |
|---|---|
| Inicio | Indicadores: total de activos, valor en uso, asignados, en mantenimiento, gráficos por estado y tipo, últimos movimientos |
| Activos | Registrar, editar, mantenimiento, baja y reactivación; filtro por estado, búsqueda, historial de cada activo, exportación a CSV |
| Asignaciones | Asignar un activo disponible a un empleado y registrar su devolución |
| Empleados | Alta, edición, activar/desactivar (no se desactiva con activos asignados) |
| Usuarios | Crear cuentas, vincularlas a un empleado, cambiar contraseña, activar/desactivar |
| Mis activos | El empleado consulta lo que tiene (o tuvo) asignado |

## Requisitos
- JDK 21 · Maven 3.9+ · NetBeans (Jakarta EE 10) · MySQL 8
- Servidor: Apache Tomcat 10.1 / 11 (válido para este avance) o Payara Server 6 (ver `docs/GUIA_PAYARA.md`)

## Puesta en marcha
1. Ejecutar `sql/01_gestor_activos.sql` en MySQL 8 (crea la BD `gestor_activos` con datos de prueba).
2. Ajustar usuario/clave en `src/main/resources/db.properties`.
3. Abrir el proyecto en NetBeans → *Clean and Build* → *Run*.
4. Ingresar en `http://localhost:8080/` (o la URL que muestre NetBeans)
   - Administrador: `admin` / `admin123`
   - Empleado: `jperez` / `empleado123`

## Pruebas unitarias
`mvn test` (o clic derecho en el proyecto → *Test* en NetBeans). Son 35 pruebas de la capa de servicio con DAO falsos: no necesitan MySQL ni servidor.

## Estructura (MVC + capas)
```
controller/  Servlets (C)      Login, Inicio, Activo, Asignacion, Empleado, Usuario, MisActivos
filter/      Seguridad         CabecerasSeguridad, Csrf, Auth, Admin   (orden definido en web.xml)
service/     Reglas de negocio Activo, Asignacion, Empleado, Usuario, Auth, Dashboard
dao/         Acceso a datos    interfaces + DAOFactory (Factory)  |  dao/impl: JDBC
model/       Entidades (M)     Activo, Empleado, Usuario, TipoActivo, EstadoActivo
dto/         Transferencia     ActivoDTO, AsignacionDTO, HistorialDTO, UsuarioDTO, ResumenDTO
util/        ConexionBD (Singleton), PasswordUtil (PBKDF2), LoginThrottle, excepciones
webapp/WEB-INF/views/  JSP + JSTL (V)
src/test/    JUnit 5 + DAO falsos
```

## Seguridad
- Contraseñas con PBKDF2-HMAC-SHA256, sal aleatoria y 120 000 iteraciones
- Token CSRF en todos los formularios POST
- Bloqueo temporal tras 5 intentos fallidos de inicio de sesión (por usuario e IP)
- Roles (administrador / empleado) verificados en el servidor; cambio de ID de sesión al ingresar
- `PreparedStatement` en todo el SQL, salida escapada con `<c:out>`, cabeceras de seguridad, cookie de sesión HttpOnly
- Exportación CSV protegida contra inyección de fórmulas

## Trabajo en equipo
Ver `docs/GUIA_GITHUB.md`.
