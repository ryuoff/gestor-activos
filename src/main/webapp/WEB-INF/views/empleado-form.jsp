<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>${empleado.idEmpleado == 0 ? 'Nuevo empleado' : 'Editar empleado'} · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="empleados"/>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1>${empleado.idEmpleado == 0 ? 'Nuevo empleado' : 'Editar empleado'}</h1>
       <p>El correo y el DNI no pueden repetirse entre empleados.</p></div>
  <a class="btn" href="${pageContext.request.contextPath}/empleados"><svg><use href="#i-atras"/></svg>Volver a empleados</a>
</div>
<section class="panel" style="max-width:760px">
  <div class="cuerpo">
  <form class="grid" method="post" action="${pageContext.request.contextPath}/empleados/guardar">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="idEmpleado" value="${empleado.idEmpleado}">
    <div><label for="dni">DNI</label>
         <input id="dni" name="dni" inputmode="numeric" pattern="[0-9]{8}" maxlength="8" required value="<c:out value='${empleado.dni}'/>">
         <div class="ayuda">8 dígitos.</div></div>
    <div><label for="area">Área</label>
         <input id="area" name="area" maxlength="60" required placeholder="Sistemas" value="<c:out value='${empleado.area}'/>"></div>
    <div><label for="nombres">Nombres</label>
         <input id="nombres" name="nombres" maxlength="80" required value="<c:out value='${empleado.nombres}'/>"></div>
    <div><label for="apellidos">Apellidos</label>
         <input id="apellidos" name="apellidos" maxlength="80" required value="<c:out value='${empleado.apellidos}'/>"></div>
    <div class="full"><label for="correo">Correo</label>
         <input id="correo" name="correo" type="email" maxlength="120" required placeholder="nombre@empresa.pe" value="<c:out value='${empleado.correo}'/>"></div>
    <div class="full acciones">
      <button class="btn principal" type="submit">Guardar empleado</button>
      <a class="btn" href="${pageContext.request.contextPath}/empleados">Cancelar</a>
    </div>
  </form>
  </div>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
