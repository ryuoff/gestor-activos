<%@ page contentType="text/html;charset=UTF-8" language="java" isErrorPage="true" %>
<%@ taglib prefix="c" uri="jakarta.tags.core" %>
<c:set var="codigo" value="${requestScope['jakarta.servlet.error.status_code']}"/>
<!DOCTYPE html>
<html lang="es">
<head>
  <meta charset="UTF-8"><meta name="viewport" content="width=device-width, initial-scale=1">
  <title>Error ${codigo} · Gestor de Activos</title>
  <link rel="stylesheet" href="${pageContext.request.contextPath}/css/estilos.css">
</head>
<body>
<div class="ingreso" style="grid-template-columns:1fr">
  <section class="form">
    <div style="max-width:460px">
      <h2 style="font-size:1.5rem">
        <c:choose>
          <c:when test="${codigo == 403}">No tienes permiso para esto</c:when>
          <c:when test="${codigo == 404}">No encontramos esa página</c:when>
          <c:otherwise>Algo salió mal</c:otherwise>
        </c:choose>
      </h2>
      <p class="sub">
        <c:choose>
          <c:when test="${codigo == 403}"><c:out value="${empty requestScope['jakarta.servlet.error.message'] ? 'Tu cuenta no puede acceder a esta opción.' : requestScope['jakarta.servlet.error.message']}"/></c:when>
          <c:when test="${codigo == 404}">La dirección no existe o el registro fue eliminado.</c:when>
          <c:otherwise>Ocurrió un error inesperado. Vuelve a intentarlo; si continúa, avisa al administrador.</c:otherwise>
        </c:choose>
      </p>
      <a class="btn principal" href="${pageContext.request.contextPath}/" style="width:auto">Ir al inicio</a>
    </div>
  </section>
</div>
</body>
</html>
