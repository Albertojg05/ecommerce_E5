/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/JSP_Servlet/Servlet.java to edit this template
 */
package com.mycompany.ecommerce_e5.servlets.cliente;

import com.mycompany.ecommerce_e5.bo.PedidoBO;
import com.mycompany.ecommerce_e5.bo.UsuarioBO;
import com.mycompany.ecommerce_e5.dao.DireccionDAO;
import com.mycompany.ecommerce_e5.dominio.Direccion;
import com.mycompany.ecommerce_e5.dominio.Pedido;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.excepciones.BusinessException;
import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.*;
import java.io.IOException;
import java.util.List;

/**
 * Servlet de la cuenta del cliente.
 * Permite ver y editar el perfil del usuario, cambiar contrasena
 * y consultar el historial de pedidos. Tambien permite cancelar
 * pedidos que aun no han sido entregados.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
@WebServlet(name = "ClienteCuentaServlet", urlPatterns = {"/cliente/cuenta"})
public class ClienteCuentaServlet extends HttpServlet {

    private PedidoBO pedidoBO;
    private UsuarioBO usuarioBO;
    private DireccionDAO direccionDAO;

    private static final int MAX_DIRECCIONES = 3;

    @Override
    public void init() {
        pedidoBO = new PedidoBO();
        usuarioBO = new UsuarioBO();
        direccionDAO = new DireccionDAO();
    }

    @Override
    protected void doGet(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");
        request.setAttribute("usuario", usuario);

        if ("editar".equals(accion)) {
            // Cargar direcciones del usuario
            List<Direccion> direcciones = direccionDAO.obtenerPorUsuario(usuario.getId());
            request.setAttribute("direcciones", direcciones);
            request.setAttribute("maxDirecciones", MAX_DIRECCIONES);
            request.setAttribute("puedeAgregarDireccion", direcciones.size() < MAX_DIRECCIONES);
            request.getRequestDispatcher("/cliente/cuenta-editar.jsp").forward(request, response);
        } else {
            List<Pedido> historial = pedidoBO.obtenerPorUsuario(usuario.getId());
            request.setAttribute("historial", historial);
            request.getRequestDispatcher("/cliente/cuenta.jsp").forward(request, response);
        }
    }

    @Override
    protected void doPost(HttpServletRequest request, HttpServletResponse response)
            throws ServletException, IOException {
        HttpSession session = request.getSession();
        Usuario usuario = (Usuario) session.getAttribute("usuarioLogueado");

        if (usuario == null) {
            response.sendRedirect(request.getContextPath() + "/login");
            return;
        }

        String accion = request.getParameter("accion");

        if ("cancelar".equals(accion)) {
            cancelarPedido(request, response, usuario);
            return;
        } else if ("agregarDireccion".equals(accion)) {
            agregarDireccion(request, response, usuario);
            return;
        } else if ("editarDireccion".equals(accion)) {
            editarDireccion(request, response, usuario);
            return;
        } else if ("eliminarDireccion".equals(accion)) {
            eliminarDireccion(request, response, usuario);
            return;
        } else if ("actualizar".equals(accion)) {
            String nombre = request.getParameter("nombre");
            String correo = request.getParameter("email");
            String telefono = request.getParameter("telefono");
            String contrasenaActual = request.getParameter("contrasenaActual");
            String nuevaContrasena = request.getParameter("nuevaContrasena");
            String confirmarContrasena = request.getParameter("confirmarContrasena");

            try {
                // Validar que el nombre no esté vacío
                if (nombre == null || nombre.trim().isEmpty()) {
                    throw new BusinessException("El nombre es requerido");
                }

                // Validar que el correo no esté vacío
                if (correo == null || correo.trim().isEmpty()) {
                    throw new BusinessException("El correo es requerido");
                }

                // #34: Verificar si el email ya existe (si cambió)
                if (!correo.equalsIgnoreCase(usuario.getCorreo())) {
                    Usuario existente = usuarioBO.buscarPorCorreo(correo);
                    if (existente != null && existente.getId() != usuario.getId()) {
                        throw new BusinessException("El correo electrónico ya está registrado por otro usuario");
                    }
                }

                // #35: Validar contraseña actual cuando se quiere cambiar
                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    if (contrasenaActual == null || contrasenaActual.isEmpty()) {
                        throw new BusinessException("Debes ingresar tu contraseña actual para cambiarla");
                    }
                    if (nuevaContrasena.length() < 6) {
                        throw new BusinessException("La nueva contraseña debe tener al menos 6 caracteres");
                    }
                    if (!nuevaContrasena.equals(confirmarContrasena)) {
                        throw new BusinessException("Las nuevas contraseñas no coinciden");
                    }
                }

                usuario.setNombre(nombre.trim());
                usuario.setCorreo(correo.trim());
                usuario.setTelefono(telefono != null ? telefono.trim() : null);

                // Primero actualizar datos personales (nombre, correo, teléfono)
                Usuario usuarioActualizado = usuarioBO.actualizar(usuario);

                // Luego cambiar contraseña si se proporcionó una nueva
                if (nuevaContrasena != null && !nuevaContrasena.isEmpty()) {
                    usuarioBO.cambiarContrasena(usuarioActualizado.getId(), contrasenaActual, nuevaContrasena);
                    // Refrescar el usuario para obtener la nueva contraseña hasheada
                    usuarioActualizado = usuarioBO.obtenerPorId(usuarioActualizado.getId());
                }

                session.setAttribute("usuarioLogueado", usuarioActualizado);
                response.sendRedirect(request.getContextPath() + "/cliente/cuenta?mensaje=actualizado");
            } catch (BusinessException e) {
                request.setAttribute("error", e.getMessage());
                request.setAttribute("usuario", usuario);
                cargarDireccionesYMostrar(request, response, usuario);
            } catch (Exception e) {
                request.setAttribute("error", "Error al actualizar: " + e.getMessage());
                request.setAttribute("usuario", usuario);
                cargarDireccionesYMostrar(request, response, usuario);
            }
        } else {
            doGet(request, response);
        }
    }

    private void cancelarPedido(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        try {
            int pedidoId = Integer.parseInt(request.getParameter("pedidoId"));
            pedidoBO.cancelarPedido(pedidoId);
            response.sendRedirect(request.getContextPath() + "/cliente/cuenta?mensaje=cancelado");
        } catch (Exception e) {
            request.setAttribute("error", "Error al cancelar: " + e.getMessage());
            request.setAttribute("usuario", usuario);
            List<Pedido> historial = pedidoBO.obtenerPorUsuario(usuario.getId());
            request.setAttribute("historial", historial);
            request.getRequestDispatcher("/cliente/cuenta.jsp").forward(request, response);
        }
    }

    private void agregarDireccion(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        try {
            // Verificar límite de direcciones
            List<Direccion> direccionesActuales = direccionDAO.obtenerPorUsuario(usuario.getId());
            if (direccionesActuales.size() >= MAX_DIRECCIONES) {
                request.setAttribute("error", "Has alcanzado el límite máximo de " + MAX_DIRECCIONES + " direcciones.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            String calle = request.getParameter("calle");
            String ciudad = request.getParameter("ciudad");
            String estado = request.getParameter("estado");
            String codigoPostal = request.getParameter("codigoPostal");

            // Validaciones básicas
            if (calle == null || calle.trim().isEmpty() ||
                ciudad == null || ciudad.trim().isEmpty() ||
                estado == null || estado.trim().isEmpty() ||
                codigoPostal == null || codigoPostal.trim().isEmpty()) {
                request.setAttribute("error", "Todos los campos de la dirección son obligatorios.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            Direccion direccion = new Direccion();
            direccion.setCalle(calle.trim());
            direccion.setCiudad(ciudad.trim());
            direccion.setEstado(estado.trim());
            direccion.setCodigoPostal(codigoPostal.trim());
            direccion.setUsuario(usuario);

            direccionDAO.guardar(direccion);
            response.sendRedirect(request.getContextPath() + "/cliente/cuenta?accion=editar&mensaje=direccionAgregada");

        } catch (Exception e) {
            request.setAttribute("error", "Error al agregar dirección: " + e.getMessage());
            cargarDireccionesYMostrar(request, response, usuario);
        }
    }

    private void editarDireccion(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        try {
            int direccionId = Integer.parseInt(request.getParameter("direccionId"));
            String calle = request.getParameter("calle");
            String ciudad = request.getParameter("ciudad");
            String estado = request.getParameter("estado");
            String codigoPostal = request.getParameter("codigoPostal");

            // Validaciones básicas
            if (calle == null || calle.trim().isEmpty() ||
                ciudad == null || ciudad.trim().isEmpty() ||
                estado == null || estado.trim().isEmpty() ||
                codigoPostal == null || codigoPostal.trim().isEmpty()) {
                request.setAttribute("error", "Todos los campos de la dirección son obligatorios.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            Direccion direccion = direccionDAO.obtenerPorId(direccionId);

            // Verificar que la dirección pertenece al usuario
            if (direccion == null || direccion.getUsuario().getId() != usuario.getId()) {
                request.setAttribute("error", "Dirección no encontrada o no autorizada.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            direccion.setCalle(calle.trim());
            direccion.setCiudad(ciudad.trim());
            direccion.setEstado(estado.trim());
            direccion.setCodigoPostal(codigoPostal.trim());

            direccionDAO.actualizar(direccion);
            response.sendRedirect(request.getContextPath() + "/cliente/cuenta?accion=editar&mensaje=direccionActualizada");

        } catch (Exception e) {
            request.setAttribute("error", "Error al actualizar dirección: " + e.getMessage());
            cargarDireccionesYMostrar(request, response, usuario);
        }
    }

    private void eliminarDireccion(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        try {
            int direccionId = Integer.parseInt(request.getParameter("direccionId"));

            Direccion direccion = direccionDAO.obtenerPorId(direccionId);

            // Verificar que la dirección pertenece al usuario
            if (direccion == null || direccion.getUsuario().getId() != usuario.getId()) {
                request.setAttribute("error", "Dirección no encontrada o no autorizada.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            // #37: Verificar si la dirección tiene pedidos asociados
            List<Pedido> pedidosConDireccion = pedidoBO.obtenerPorDireccion(direccionId);
            if (pedidosConDireccion != null && !pedidosConDireccion.isEmpty()) {
                request.setAttribute("error", "No se puede eliminar esta dirección porque tiene "
                        + pedidosConDireccion.size() + " pedido(s) asociado(s). "
                        + "Puedes editarla en su lugar.");
                cargarDireccionesYMostrar(request, response, usuario);
                return;
            }

            direccionDAO.eliminar(direccionId);
            response.sendRedirect(request.getContextPath() + "/cliente/cuenta?accion=editar&mensaje=direccionEliminada");

        } catch (Exception e) {
            request.setAttribute("error", "Error al eliminar dirección: " + e.getMessage());
            cargarDireccionesYMostrar(request, response, usuario);
        }
    }

    private void cargarDireccionesYMostrar(HttpServletRequest request, HttpServletResponse response, Usuario usuario)
            throws ServletException, IOException {
        List<Direccion> direcciones = direccionDAO.obtenerPorUsuario(usuario.getId());
        request.setAttribute("direcciones", direcciones);
        request.setAttribute("maxDirecciones", MAX_DIRECCIONES);
        request.setAttribute("puedeAgregarDireccion", direcciones.size() < MAX_DIRECCIONES);
        request.setAttribute("usuario", usuario);
        request.getRequestDispatcher("/cliente/cuenta-editar.jsp").forward(request, response);
    }

    @Override
    public String getServletInfo() {
        return "Cuenta Cliente";
    }
}
