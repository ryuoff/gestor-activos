<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Usuarios · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="usuarios"/>
<%@ include file="_encabezado.jspf" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<div class="cabecera">
  <div><h1>Usuarios</h1><p>Cuentas para ingresar al sistema. Los empleados solo pueden consultar sus activos.</p></div>
  <a class="btn principal" href="${base}/usuarios/nuevo"><svg><use href="#i-mas"/></svg>Nuevo usuario</a>
</div>
<section class="panel">
  <c:choose>
  <c:when test="${empty usuarios}"><div class="vacio"><strong>No hay usuarios</strong>Crea la primera cuenta.</div></c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table>
    <thead><tr><th>Usuario</th><th>Rol</th><th>Empleado vinculado</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>
    <c:forEach var="u" items="${usuarios}">
      <tr class="${u.activo ? '' : 'inactivo'}">
        <td><div class="quien"><span class="avatar"><c:out value="${u.iniciales}"/></span><span class="nombre"><c:out value="${u.username}"/></span></div></td>
        <td><span class="rol ${u.administrador ? 'admin' : ''}"><c:out value="${u.administrador ? 'Administrador' : 'Empleado'}"/></span></td>
        <td><c:out value="${empty u.empleado ? '—' : u.empleado}"/></td>
        <td><span class="estado ${u.activo ? 'e-ACTIVO' : 'e-INACTIVO'}"><c:out value="${u.activo ? 'Activo' : 'Inactivo'}"/></span></td>
        <td>
          <div class="acciones">
            <a class="btn sm" href="${base}/usuarios/clave?id=${u.idUsuario}">Cambiar contraseña</a>
            <c:choose>
              <c:when test="${u.activo}">
                <form method="post" action="${base}/usuarios/estado" data-titulo="Desactivar usuario" data-boton="Desactivar" data-confirmar="${fn:escapeXml(u.username)} ya no podrá iniciar sesión.">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                  <input type="hidden" name="id" value="${u.idUsuario}"><input type="hidden" name="activo" value="false">
                  <button class="btn sm peligro" type="submit">Desactivar</button></form>
              </c:when>
              <c:otherwise>
                <form method="post" action="${base}/usuarios/estado">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                  <input type="hidden" name="id" value="${u.idUsuario}"><input type="hidden" name="activo" value="true">
                  <button class="btn sm" type="submit">Activar</button></form>
              </c:otherwise>
            </c:choose>
          </div>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  </div>
  </c:otherwise>
  </c:choose>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
