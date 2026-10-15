<%@ page contentType="text/html;charset=UTF-8" language="java" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Iniciar sesión · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="ingreso">
  <section class="lado">
    <div class="marca"><span class="logo"><svg viewBox="0 0 24 24" fill="none" stroke="#fff" stroke-width="2.2" stroke-linecap="round" stroke-linejoin="round"><path d="M21 8l-9-5-9 5 9 5 9-5z"/><path d="M3 8v8l9 5 9-5V8"/></svg></span>Gestor de Activos</div>
    <div>
      <h1>Cada activo, con dueño y con historia.</h1>
      <p>Registra los equipos de la empresa, asígnalos a un responsable y consulta en qué estado está cada uno.</p>
      <div class="etiquetas" aria-hidden="true">
        <div class="etiqueta"><span class="codigo">AC-0001</span><span>Laptop Dell Latitude · Asignado</span></div>
        <div class="etiqueta"><span class="codigo">AC-0004</span><span>Escritorio ejecutivo · Disponible</span></div>
        <div class="etiqueta"><span class="codigo">AC-0005</span><span>Taladro Bosch · En mantenimiento</span></div>
      </div>
    </div>
    <small style="color:#8b97b8">Desarrollo Web Integrado · UTP</small>
  </section>
  <section class="form">
    <form method="post" action="${pageContext.request.contextPath}/login">
    <input type="hidden" name="_csrf" value="${sessionScope.csrf}">
      <h2>Iniciar sesión</h2>
      <p class="sub">Ingresa con tu usuario del sistema.</p>
      <c:if test="${not empty error}"><div class="aviso err" role="alert"><span><c:out value="${error}"/></span></div></c:if>
      <div class="campo"><label for="username">Usuario</label><input id="username" name="username" autocomplete="username" required autofocus></div>
      <div class="campo"><label for="password">Contraseña</label><input id="password" name="password" type="password" autocomplete="current-password" required></div>
      <button class="btn principal" type="submit">Iniciar sesión</button>
      <div class="pruebas">Cuentas de prueba<br><code>admin</code> / <code>admin123</code> (administrador)<br><code>jperez</code> / <code>empleado123</code> (empleado)</div>
    </form>
  </section>
</div>
</body>
</html>
