package pe.edu.utp.gestoractivos.controller;

import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpSession;

/** Mensajes "flash": se guardan en sesión antes de redirigir y se muestran una sola vez. */
final class Mensajes {
    private Mensajes() { }

    static void ok(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("mensaje", texto);
    }

    static void error(HttpServletRequest req, String texto) {
        req.getSession().setAttribute("error", texto);
    }

    /** Pasa los mensajes pendientes de la sesión a la solicitud actual (y los borra de la sesión). */
    static void mostrar(HttpServletRequest req) {
        HttpSession s = req.getSession();
        for (String clave : new String[]{"mensaje", "error"}) {
            Object v = s.getAttribute(clave);
            if (v != null) {
                req.setAttribute(clave, v);
                s.removeAttribute(clave);
            }
        }
    }

    static int entero(String s) {
        try {
            return Integer.parseInt(s);
        } catch (NumberFormatException | NullPointerException e) {
            return 0;
        }
    }
}
