<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Mis activos · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1>Mis activos</h1><p>Los activos que tienes asignados y los que ya devolviste.</p></div>
</div>
<section class="panel">
  <c:choose>
  <c:when test="${empty asignaciones}"><div class="vacio"><strong>No tienes activos asignados</strong>Cuando el administrador te asigne uno, lo verás aquí.</div></c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table>
    <thead><tr><th>Activo</th><th>Estado</th><th>Recibido</th><th>Devuelto</th><th>Observación</th></tr></thead>
    <tbody>
    <c:forEach var="s" items="${asignaciones}">
      <tr>
        <td><span class="codigo"><c:out value="${s.codigoActivo}"/></span> <span class="nombre"><c:out value="${s.nombreActivo}"/></span></td>
        <td><span class="estado ${s.vigente ? 'e-ASIGNADO' : 'e-DISPONIBLE'}"><c:out value="${s.vigente ? 'En uso' : 'Devuelto'}"/></span></td>
        <td><c:out value="${s.fechaAsignacionTexto}"/></td>
        <td><c:out value="${s.fechaDevolucionTexto}"/></td>
        <td class="detalle"><c:out value="${empty s.observacion ? '—' : s.observacion}"/></td>
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
