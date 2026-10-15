<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Cambiar contraseña · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<c:set var="pagina" value="usuarios"/>
<%@ include file="_encabezado.jspf" %>
<div class="cabecera">
  <div><h1>Cambiar contraseña</h1><p>Cuenta: <strong><c:out value="${cuenta.username}"/></strong></p></div>
  <a class="btn" href="${pageContext.request.contextPath}/usuarios"><svg><use href="#i-atras"/></svg>Volver a usuarios</a>
</div>
<section class="panel" style="max-width:520px">
  <div class="cuerpo">
  <form method="post" action="${pageContext.request.contextPath}/usuarios/clave" autocomplete="off">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
    <input type="hidden" name="id" value="${cuenta.idUsuario}">
    <label for="password">Nueva contraseña</label>
    <input id="password" name="password" type="password" minlength="8" required autocomplete="new-password" autofocus>
    <div class="ayuda" style="margin-bottom:16px">Mínimo 8 caracteres, con letras y números.</div>
    <div class="acciones">
      <button class="btn principal" type="submit">Guardar contraseña</button>
      <a class="btn" href="${pageContext.request.contextPath}/usuarios">Cancelar</a>
    </div>
  </form>
  </div>
</section>
<%@ include file="_pie.jspf" %>
</body>
</html>
