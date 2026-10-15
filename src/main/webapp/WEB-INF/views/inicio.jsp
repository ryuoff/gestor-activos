<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Inicio · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="inicio"/>
<%@ include file="_encabezado.jspf" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<div class="cabecera">
  <div><h1>Inicio</h1><p>Un vistazo al inventario y a los últimos movimientos.</p></div>
  <a class="btn principal" href="${base}/activos/nuevo"><svg><use href="#i-mas"/></svg>Nuevo activo</a>
</div>

<nav class="resumen cuatro" aria-label="Indicadores">
  <a href="${base}/activos"><span class="n">${resumen.total}</span><span class="t">Activos registrados</span></a>
  <a href="${base}/activos"><span class="n" style="font-size:1.5rem;padding-top:5px">S/ <fmt:formatNumber value="${resumen.valorTotal}" minFractionDigits="2" maxFractionDigits="2"/></span><span class="t">Valor del inventario en uso</span></a>
  <a href="${base}/asignaciones"><span class="n">${resumen.asignados}</span><span class="t"><span class="punto e-ASIGNADO"></span>Asignados ahora</span></a>
  <a href="${base}/activos?estado=MANTENIMIENTO"><span class="n">${resumen.enMantenimiento}</span><span class="t"><span class="punto e-MANTENIMIENTO"></span>En mantenimiento</span></a>
</nav>

<div class="dos">
  <section class="panel">
    <header><h2>Activos por estado</h2></header>
    <div class="cuerpo barras">
      <c:forEach var="e" items="${estados}">
        <c:set var="n" value="${resumen.porEstado[e]}"/>
        <fmt:formatNumber var="pct" value="${resumen.total == 0 ? 0 : n * 100.0 / resumen.total}" maxFractionDigits="0" groupingUsed="false"/>
        <div class="barra-fila e-${e}">
          <span class="estado e-${e}"><c:out value="${e.etiqueta}"/></span>
          <div class="pista"><span style="width:${pct}%"></span></div>
          <span class="num">${n}</span>
        </div>
      </c:forEach>
    </div>
  </section>
  <section class="panel">
    <header><h2>Activos por tipo</h2></header>
    <div class="cuerpo barras">
      <c:if test="${empty resumen.porTipo}"><div class="vacio" style="padding:10px"><strong>Aún no hay activos</strong>Registra el primero para ver el detalle.</div></c:if>
      <c:forEach var="t" items="${resumen.porTipo}">
        <fmt:formatNumber var="pct" value="${resumen.total == 0 ? 0 : t.value * 100.0 / resumen.total}" maxFractionDigits="0" groupingUsed="false"/>
        <div class="barra-fila">
          <span><c:out value="${t.key}"/></span>
          <div class="pista"><span style="width:${pct}%"></span></div>
          <span class="num">${t.value}</span>
        </div>
      </c:forEach>
    </div>
  </section>
</div>

<section class="panel">
  <header><h2>Movimientos recientes</h2><a class="btn sm" href="${base}/activos">Ver activos</a></header>
  <c:choose>
  <c:when test="${empty resumen.movimientos}"><div class="vacio"><strong>Sin movimientos</strong>Los cambios de estado aparecerán aquí.</div></c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table>
    <thead><tr><th>Fecha</th><th>Activo</th><th>Cambio</th><th>Usuario</th><th>Detalle</th></tr></thead>
    <tbody>
    <c:forEach var="m" items="${resumen.movimientos}">
      <tr>
        <td style="white-space:nowrap"><c:out value="${m.fechaTexto}"/></td>
        <td><span class="codigo"><c:out value="${m.codigoActivo}"/></span> <c:out value="${m.nombreActivo}"/></td>
        <td style="white-space:nowrap"><c:if test="${not empty m.estadoAnterior}"><span class="estado e-${m.estadoAnterior}"><c:out value="${m.estadoAnterior}"/></span> → </c:if><span class="estado e-${m.estadoNuevo}"><c:out value="${m.estadoNuevo}"/></span></td>
        <td><c:out value="${m.usuario}"/></td>
        <td class="detalle"><c:out value="${m.detalle}"/></td>
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
