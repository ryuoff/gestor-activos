<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Empleados · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="empleados"/>
<%@ include file="_encabezado.jspf" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<div class="cabecera">
  <div><h1>Empleados</h1><p>Personas que pueden recibir activos. Un empleado desactivado ya no aparece al asignar.</p></div>
  <a class="btn principal" href="${base}/empleados/nuevo"><svg><use href="#i-mas"/></svg>Nuevo empleado</a>
</div>
<section class="panel">
  <header>
    <h2>Lista de empleados</h2>
    <div class="buscador"><svg><use href="#i-buscar"/></svg><input id="buscar" type="search" placeholder="Buscar empleado" aria-label="Buscar empleados"></div>
  </header>
  <c:choose>
  <c:when test="${empty empleados}"><div class="vacio"><strong>Todavía no hay empleados</strong>Registra el primero para poder asignarle activos.</div></c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table id="tabla">
    <thead><tr><th>Empleado</th><th>DNI</th><th>Correo</th><th>Área</th><th>Estado</th><th>Acciones</th></tr></thead>
    <tbody>
    <c:forEach var="e" items="${empleados}">
      <tr class="${e.activo ? '' : 'inactivo'}">
        <td><div class="quien"><span class="avatar"><c:out value="${fn:substring(e.nombres,0,1)}${fn:substring(e.apellidos,0,1)}"/></span><span class="nombre"><c:out value="${e.nombreCompleto}"/></span></div></td>
        <td><span class="codigo"><c:out value="${e.dni}"/></span></td>
        <td><c:out value="${e.correo}"/></td>
        <td><c:out value="${e.area}"/></td>
        <td><span class="estado ${e.activo ? 'e-ACTIVO' : 'e-INACTIVO'}"><c:out value="${e.activo ? 'Activo' : 'Inactivo'}"/></span></td>
        <td>
          <div class="acciones">
            <a class="btn sm" href="${base}/empleados/editar?id=${e.idEmpleado}">Editar</a>
            <c:choose>
              <c:when test="${e.activo}">
                <form method="post" action="${base}/empleados/estado" data-titulo="Desactivar empleado" data-boton="Desactivar" data-confirmar="${fn:escapeXml(e.nombreCompleto)} ya no podrá recibir activos. Si tiene activos asignados, primero debes registrar su devolución.">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                  <input type="hidden" name="id" value="${e.idEmpleado}"><input type="hidden" name="activo" value="false">
                  <button class="btn sm peligro" type="submit">Desactivar</button></form>
              </c:when>
              <c:otherwise>
                <form method="post" action="${base}/empleados/estado">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                  <input type="hidden" name="id" value="${e.idEmpleado}"><input type="hidden" name="activo" value="true">
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
  <div class="vacio" id="sin-resultados" hidden><strong>Sin coincidencias</strong>Prueba con otro nombre, DNI o área.</div>
  </c:otherwise>
  </c:choose>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
