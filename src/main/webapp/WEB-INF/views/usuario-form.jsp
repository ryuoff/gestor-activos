<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Nuevo usuario · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="usuarios"/>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1>Nuevo usuario</h1><p>Crea una cuenta y, si es de un empleado, vincúlala para que vea sus activos.</p></div>
  <a class="btn" href="${pageContext.request.contextPath}/usuarios"><svg><use href="#i-atras"/></svg>Volver a usuarios</a>
</div>
<section class="panel" style="max-width:760px">
  <div class="cuerpo">
  <form class="grid" method="post" action="${pageContext.request.contextPath}/usuarios/guardar" autocomplete="off">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
    <div><label for="username">Usuario</label>
         <input id="username" name="username" maxlength="40" required autocomplete="off" value="<c:out value='${username}'/>">
         <div class="ayuda">Letras, números, punto, guion o guion bajo.</div></div>
    <div><label for="password">Contraseña</label>
         <input id="password" name="password" type="password" minlength="8" required autocomplete="new-password">
         <div class="ayuda">Mínimo 8 caracteres, con letras y números.</div></div>
    <div><label for="rol">Rol</label>
         <select id="rol" name="rol" required>
           <option value="EMPLEADO" ${rol == 'EMPLEADO' ? 'selected' : ''}>Empleado (solo consulta)</option>
           <option value="ADMINISTRADOR" ${rol == 'ADMINISTRADOR' ? 'selected' : ''}>Administrador (acceso completo)</option>
         </select></div>
    <div><label for="idEmpleado">Empleado vinculado</label>
         <select id="idEmpleado" name="idEmpleado">
           <option value="0">Sin vincular</option>
           <c:forEach var="e" items="${empleados}">
             <option value="${e.idEmpleado}" ${idEmpleado == e.idEmpleado ? 'selected' : ''}><c:out value="${e.nombreCompleto} (${e.area})"/></option>
           </c:forEach>
         </select>
         <div class="ayuda">Obligatorio si el rol es Empleado. Solo aparecen empleados sin cuenta.</div></div>
    <div class="full acciones">
      <button class="btn principal" type="submit">Crear usuario</button>
      <a class="btn" href="${pageContext.request.contextPath}/usuarios">Cancelar</a>
    </div>
  </form>
  </div>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
