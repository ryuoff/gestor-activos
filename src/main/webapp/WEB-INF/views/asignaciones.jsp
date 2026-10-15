<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Asignaciones · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="asignaciones"/>
<%@ include file="_encabezado.jspf" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<div class="cabecera">
  <div><h1>Asignaciones</h1><p>Entrega activos disponibles a un empleado y registra cuando los devuelve.</p></div>
</div>

<section class="panel">
  <header><h2>Asignar un activo</h2></header>
  <div class="cuerpo">
  <c:choose>
  <c:when test="${empty disponibles}"><div class="vacio" style="padding:12px"><strong>No hay activos disponibles</strong>Registra un activo nuevo o registra una devolución para poder asignar.</div></c:when>
  <c:otherwise>
  <form class="grid" method="post" action="${base}/asignaciones/asignar">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
    <div><label for="idActivo">Activo disponible</label>
      <select id="idActivo" name="idActivo" required>
        <option value="">Selecciona un activo</option>
        <c:forEach var="a" items="${disponibles}"><option value="${a.idActivo}"><c:out value="${a.codigo} · ${a.nombre}"/></option></c:forEach>
      </select></div>
    <div><label for="idEmpleado">Empleado</label>
      <select id="idEmpleado" name="idEmpleado" required>
        <option value="">Selecciona un empleado</option>
        <c:forEach var="e" items="${empleados}"><option value="${e.idEmpleado}"><c:out value="${e.nombreCompleto} (${e.area})"/></option></c:forEach>
      </select></div>
    <div class="full"><label for="observacion">Observación</label>
      <input id="observacion" name="observacion" maxlength="255" placeholder="Accesorios entregados, condiciones, etc.">
      <div class="ayuda">Opcional. Queda guardada junto con la asignación.</div></div>
    <div class="full"><button class="btn principal" type="submit"><svg><use href="#i-asignar"/></svg>Asignar activo</button></div>
  </form>
  </c:otherwise>
  </c:choose>
  </div>
</section>

<section class="panel">
  <header><h2>Asignaciones vigentes</h2></header>
  <c:choose>
  <c:when test="${empty vigentes}"><div class="vacio"><strong>No hay asignaciones vigentes</strong>Los activos asignados aparecerán aquí.</div></c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table>
    <thead><tr><th>Activo</th><th>Empleado</th><th>Desde</th><th>Observación</th><th></th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${vigentes}">
      <tr>
        <td><span class="codigo"><c:out value="${s.codigoActivo}"/></span> <span class="nombre"><c:out value="${s.nombreActivo}"/></span></td>
        <td><div class="quien"><span class="avatar"><c:out value="${s.iniciales}"/></span><div><c:out value="${s.empleado}"/><div class="detalle"><c:out value="${s.area}"/></div></div></div></td>
        <td><c:out value="${s.fechaAsignacionTexto}"/></td>
        <td class="detalle"><c:out value="${empty s.observacion ? '—' : s.observacion}"/></td>
        <td>
          <form method="post" action="${base}/asignaciones/devolver" data-titulo="Registrar devolución" data-boton="Registrar devolución" data-confirmar="${fn:escapeXml(s.codigoActivo)} volverá a estar disponible para asignarse a otra persona.">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
            <input type="hidden" name="idActivo" value="${s.idActivo}">
            <button class="btn sm" type="submit">Registrar devolución</button>
          </form>
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
