<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${activo.idActivo == 0 ? 'Nuevo activo' : 'Editar activo'} · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="activos"/>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1>${activo.idActivo == 0 ? 'Nuevo activo' : 'Editar activo'}</h1>
       <p>${activo.idActivo == 0 ? 'Se registra como disponible y queda anotado en su historial.' : 'El estado se cambia desde la lista de activos.'}</p></div>
  <a class="btn" href="${pageContext.request.contextPath}/activos"><svg><use href="#i-atras"/></svg>Volver a activos</a>
</div>
<section class="panel" style="max-width:760px">
  <div class="cuerpo">
  <form class="grid" method="post" action="${pageContext.request.contextPath}/activos/guardar">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="idActivo" value="${activo.idActivo}">
    <div><label for="codigo">Código</label>
         <input id="codigo" name="codigo" maxlength="20" required placeholder="AC-0007" value="<c:out value='${activo.codigo}'/>">
         <div class="ayuda">Debe ser único en el inventario.</div></div>
    <div><label for="nombre">Nombre</label>
         <input id="nombre" name="nombre" maxlength="100" required value="<c:out value='${activo.nombre}'/>"></div>
    <div><label for="idTipo">Tipo</label>
         <select id="idTipo" name="idTipo" required>
           <option value="">Selecciona un tipo</option>
           <c:forEach var="t" items="${tipos}">
             <option value="${t.idTipo}" ${activo.idTipo == t.idTipo ? 'selected' : ''}><c:out value="${t.nombre}"/></option>
           </c:forEach>
         </select></div>
    <div><label for="fechaAdquisicion">Fecha de adquisición</label>
         <input id="fechaAdquisicion" name="fechaAdquisicion" type="date" required value="${activo.fechaAdquisicion}"></div>
    <div><label for="valor">Valor (S/)</label>
         <input id="valor" name="valor" type="number" step="0.01" min="0" required value="${activo.valor}"></div>
    <div class="full"><label for="descripcion">Descripción</label>
         <textarea id="descripcion" name="descripcion" rows="3" maxlength="255" placeholder="Características, número de serie, accesorios"><c:out value="${activo.descripcion}"/></textarea></div>
    <div class="full acciones">
      <button class="btn principal" type="submit">Guardar activo</button>
      <a class="btn" href="${pageContext.request.contextPath}/activos">Cancelar</a>
    </div>
  </form>
  </div>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
