<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Historial · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="activos"/>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1><c:out value="${activo.nombre}"/></h1>
       <p><span class="codigo"><c:out value="${activo.codigo}"/></span> &nbsp;<span class="estado e-${activo.estado}"><c:out value="${activo.estado.etiqueta}"/></span></p></div>
  <a class="btn" href="${pageContext.request.contextPath}/activos"><svg><use href="#i-atras"/></svg>Volver a activos</a>
</div>
<section class="panel" style="max-width:760px">
  <header><h2>Historial de estados</h2></header>
  <div class="cuerpo">
  <c:choose>
  <c:when test="${empty historial}"><div class="vacio"><strong>Sin movimientos</strong>Este activo aún no tiene cambios de estado.</div></c:when>
  <c:otherwise>
  <ol class="linea">
    <c:forEach var="h" items="${historial}">
      <li class="e-${h.estadoNuevo}">
        <div class="cuando"><c:out value="${h.fechaTexto}"/> · <c:out value="${h.usuario}"/></div>
        <div class="que"><c:if test="${not empty h.estadoAnterior}"><c:out value="${h.estadoAnterior}"/> → </c:if><c:out value="${h.estadoNuevo}"/></div>
        <div class="nota"><c:out value="${h.detalle}"/></div>
      </li>
    </c:forEach>
  </ol>
  </c:otherwise>
  </c:choose>
  </div>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
