package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import java.io.IOException;
import java.math.BigDecimal;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.EnumMap;
import java.util.List;
import java.util.Map;
import pe.edu.utp.gestoractivos.dto.ActivoDTO;
import pe.edu.utp.gestoractivos.model.Activo;
import pe.edu.utp.gestoractivos.model.EstadoActivo;
import pe.edu.utp.gestoractivos.model.Usuario;
import pe.edu.utp.gestoractivos.service.ActivoService;
import pe.edu.utp.gestoractivos.util.NegocioException;

/**
 * CONTROLADOR (MVC) del módulo de activos.
 *   GET  /activos             -> lista (filtro opcional ?estado=)
 *   GET  /activos/nuevo       -> formulario de registro
 *   GET  /activos/editar?id=  -> formulario de edición
 *   GET  /activos/historial?id= -> trazabilidad del activo
 *   GET  /activos/exportar    -> descarga el inventario en CSV
 *   POST /activos/guardar     -> inserta o actualiza
 *   POST /activos/estado      -> cambia el estado (mantenimiento, baja, reactivar)
 */
@WebServlet("/activos/*")
public class ActivoServlet extends HttpServlet {
    private static final long serialVersionUID = 1L;
    private final transient ActivoService service = new ActivoService();

    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String accion = req.getPathInfo() == null ? "/" : req.getPathInfo();
        switch (accion) {
            case "/nuevo" -> formulario(req, resp, new Activo());
            case "/editar" -> {
                Activo a = service.buscar(entero(req.getParameter("id")));
                if (a == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Activo no encontrado");
                } else {
                    formulario(req, resp, a);
                }
            }
            case "/exportar" -> {
                resp.setContentType("text/csv; charset=UTF-8");
                resp.setHeader("Content-Disposition", "attachment; filename=\"activos.csv\"");
                resp.getWriter().write("\uFEFF" + service.exportarCsv());   // BOM para que Excel lea las tildes
            }
            case "/historial" -> {
                int id = entero(req.getParameter("id"));
                Activo a = service.buscar(id);
                if (a == null) {
                    resp.sendError(HttpServletResponse.SC_NOT_FOUND, "Activo no encontrado");
                    return;
                }
                req.setAttribute("activo", a);
                req.setAttribute("historial", service.historial(id));
                req.getRequestDispatcher("/WEB-INF/views/historial.jsp").forward(req, resp);
            }
            default -> listar(req, resp);
        }
    }

    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        req.setCharacterEncoding("UTF-8");
        String accion = req.getPathInfo() == null ? "" : req.getPathInfo();
        HttpSession session = req.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuario");
        try {
            switch (accion) {
                case "/guardar" -> guardar(req, resp, usuario);
                case "/estado" -> {
                    EstadoActivo nuevo = EstadoActivo.valueOf(req.getParameter("nuevo"));
                    service.cambiarEstado(entero(req.getParameter("id")), nuevo, usuario.getIdUsuario(),
                            req.getParameter("detalle"));
                    session.setAttribute("mensaje", "Estado actualizado a: " + nuevo.getEtiqueta());
                    resp.sendRedirect(req.getContextPath() + "/activos");
                }
                default -> resp.sendError(HttpServletResponse.SC_NOT_FOUND);
            }
        } catch (NegocioException e) {
            session.setAttribute("error", e.getMessage());
            resp.sendRedirect(req.getContextPath() + "/activos");
        }
    }

    private void listar(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        EstadoActivo filtro = null;
        String p = req.getParameter("estado");
        if (p != null && !p.isBlank()) {
            try {
                filtro = EstadoActivo.valueOf(p);
            } catch (IllegalArgumentException ignorar) {
                // filtro inválido: se muestra todo
            }
        }
        List<ActivoDTO> todos = service.listar(null);
        Map<EstadoActivo, Long> conteo = new EnumMap<>(EstadoActivo.class);
        for (EstadoActivo e : EstadoActivo.values()) {
            conteo.put(e, 0L);
        }
        for (ActivoDTO a : todos) {
            conteo.merge(a.getEstado(), 1L, Long::sum);
        }
        final EstadoActivo f = filtro;
        pasarMensajes(req);
        req.setAttribute("activos", f == null ? todos : todos.stream().filter(a -> a.getEstado() == f).toList());
        req.setAttribute("total", todos.size());
        req.setAttribute("conteo", conteo);
        req.setAttribute("estados", EstadoActivo.values());
        req.setAttribute("filtro", filtro);
        req.getRequestDispatcher("/WEB-INF/views/activos.jsp").forward(req, resp);
    }

    private void formulario(HttpServletRequest req, HttpServletResponse resp, Activo a)
            throws ServletException, IOException {
        req.setAttribute("activo", a);
        req.setAttribute("tipos", service.tipos());
        req.getRequestDispatcher("/WEB-INF/views/activo-form.jsp").forward(req, resp);
    }

    private void guardar(HttpServletRequest req, HttpServletResponse resp, Usuario usuario)
            throws ServletException, IOException, NegocioException {
        Activo a = new Activo();
        a.setIdActivo(entero(req.getParameter("idActivo")));
        a.setCodigo(req.getParameter("codigo"));
        a.setNombre(req.getParameter("nombre"));
        a.setDescripcion(req.getParameter("descripcion"));
        a.setIdTipo(entero(req.getParameter("idTipo")));
        try {
            a.setFechaAdquisicion(LocalDate.parse(req.getParameter("fechaAdquisicion")));
        } catch (DateTimeParseException | NullPointerException e) {
            a.setFechaAdquisicion(null);
        }
        try {
            a.setValor(new BigDecimal(req.getParameter("valor")));
        } catch (NumberFormatException | NullPointerException e) {
            a.setValor(null);
        }
        try {
            if (a.getIdActivo() == 0) {
                service.registrar(a, usuario.getIdUsuario());
                req.getSession().setAttribute("mensaje", "Activo registrado correctamente.");
            } else {
                service.editar(a);
                req.getSession().setAttribute("mensaje", "Activo actualizado correctamente.");
            }
            resp.sendRedirect(req.getContextPath() + "/activos");
        } catch (NegocioException e) {
            // Vuelve al formulario conservando lo digitado y mostrando el motivo.
            req.setAttribute("error", e.getMessage());
            formulario(req, resp, a);
        }
    }

    private void pasarMensajes(HttpServletRequest req) {
        HttpSession s = req.getSession();
        for (String clave : new String[]{"mensaje", "error"}) {
            Object v = s.getAttribute(clave);
            if (v != null) {
                req.setAttribute(clave, v);
                s.removeAttribute(clave);
            }
        }
    }

    private static int entero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
