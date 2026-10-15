<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<%@ taglib prefix="fn" uri="jakarta.tags.functions" %>
<%@ taglib prefix="fmt" uri="jakarta.tags.fmt" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Activos · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="activos"/>
<%@ include file="_encabezado.jspf" %>
<c:set var="base" value="${pageContext.request.contextPath}"/>
<div class="cabecera">
  <div><h1>Activos</h1><p>Inventario de la empresa y el estado de cada bien.</p></div>
  <div class="acciones">
    <a class="btn" href="${base}/activos/exportar"><svg><use href="#i-bajar"/></svg>Exportar CSV</a>
    <a class="btn principal" href="${base}/activos/nuevo"><svg><use href="#i-mas"/></svg>Nuevo activo</a>
  </div>
</div>

<nav class="resumen" aria-label="Filtrar por estado">
  <a href="${base}/activos" class="${empty filtro ? 'sel' : ''}"><span class="n">${total}</span><span class="t">Todos</span></a>
  <c:forEach var="e" items="${estados}">
    <a href="${base}/activos?estado=${e}" class="${filtro == e ? 'sel' : ''}">
      <span class="n">${conteo[e]}</span><span class="t"><span class="punto e-${e}" style="background:var(--c)"></span><c:out value="${e.etiqueta}"/></span>
    </a>
  </c:forEach>
</nav>

<section class="panel">
  <header>
    <h2><c:out value="${empty filtro ? 'Todos los activos' : filtro.etiqueta}"/></h2>
    <div class="buscador"><svg><use href="#i-buscar"/></svg><input id="buscar" type="search" placeholder="Buscar activo o responsable" aria-label="Buscar activos"></div>
  </header>
  <c:choose>
  <c:when test="${empty activos}">
    <div class="vacio"><strong>No hay activos en este estado</strong>Cambia el filtro o registra un activo nuevo.</div>
  </c:when>
  <c:otherwise>
  <div class="tabla-scroll">
  <table id="tabla">
    <thead><tr><th>Código</th><th>Activo</th><th>Tipo</th><th>Estado</th><th>Responsable</th><th class="num">Valor</th><th>Acciones</th></tr></thead>
    <tbody>
    <c:forEach var="a" items="${activos}">
      <tr>
        <td><span class="codigo"><c:out value="${a.codigo}"/></span></td>
        <td><div class="nombre"><c:out value="${a.nombre}"/></div><div class="detalle"><c:out value="${a.descripcion}"/></div></td>
        <td><c:out value="${a.tipo}"/></td>
        <td><span class="estado e-${a.estado}"><c:out value="${a.estado.etiqueta}"/></span></td>
        <td>
          <c:choose>
            <c:when test="${empty a.responsable}"><span class="detalle">Sin asignar</span></c:when>
            <c:otherwise><div class="quien"><span class="avatar"><c:out value="${a.inicialesResponsable}"/></span><c:out value="${a.responsable}"/></div></c:otherwise>
          </c:choose>
        </td>
        <td class="num">S/ <fmt:formatNumber value="${a.valor}" minFractionDigits="2" maxFractionDigits="2"/></td>
        <td>
          <div class="acciones">
            <a class="btn sm" href="${base}/activos/historial?id=${a.idActivo}">Historial</a>
            <c:if test="${a.estado != 'BAJA'}"><a class="btn sm" href="${base}/activos/editar?id=${a.idActivo}">Editar</a></c:if>
            <c:if test="${a.estado == 'DISPONIBLE'}">
              <form method="post" action="${base}/activos/estado" data-titulo="Enviar a mantenimiento" data-boton="Enviar" data-confirmar="${fn:escapeXml(a.codigo)} quedará en mantenimiento y no podrá asignarse hasta que lo marques como disponible.">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="id" value="${a.idActivo}"><input type="hidden" name="nuevo" value="MANTENIMIENTO"><input type="hidden" name="detalle" value="Enviado a mantenimiento">
                <button class="btn sm" type="submit">Mantenimiento</button></form>
            </c:if>
            <c:if test="${a.estado == 'MANTENIMIENTO'}">
              <form method="post" action="${base}/activos/estado">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="id" value="${a.idActivo}"><input type="hidden" name="nuevo" value="DISPONIBLE"><input type="hidden" name="detalle" value="Mantenimiento finalizado">
                <button class="btn sm" type="submit">Marcar disponible</button></form>
            </c:if>
            <c:if test="${a.estado == 'DISPONIBLE' || a.estado == 'MANTENIMIENTO'}">
              <form method="post" action="${base}/activos/estado" data-titulo="Dar de baja" data-boton="Dar de baja" data-confirmar="${fn:escapeXml(a.codigo)} saldrá de uso y no podrá asignarse ni editarse, salvo que lo reactives.">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="id" value="${a.idActivo}"><input type="hidden" name="nuevo" value="BAJA"><input type="hidden" name="detalle" value="Baja del activo">
                <button class="btn sm peligro" type="submit">Dar de baja</button></form>
            </c:if>
            <c:if test="${a.estado == 'BAJA'}">
              <form method="post" action="${base}/activos/estado">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
                <input type="hidden" name="id" value="${a.idActivo}"><input type="hidden" name="nuevo" value="DISPONIBLE"><input type="hidden" name="detalle" value="Reactivación explícita del administrador">
                <button class="btn sm" type="submit">Reactivar</button></form>
            </c:if>
          </div>
        </td>
      </tr>
    </c:forEach>
    </tbody>
  </table>
  </div>
  <div class="vacio" id="sin-resultados" hidden><strong>Sin coincidencias</strong>Prueba con otro código, nombre o responsable.</div>
  </c:otherwise>
  </c:choose>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
