/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.UsuarioDAO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.dominio.enums.RolUsuario;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * @author Alberto Jiménez García 252595 
 * Rene Ezequiel Figueroa Lopez 228691
 * Freddy Alí Castro Román 252191
 */
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;

    public UsuarioBO() {
        this.usuarioDAO = new UsuarioDAO();
    }

    /**
     * Validar login de usuario con contraseña hasheada
     */
    public Usuario login(String correo, String contrasena) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("El correo es requerido");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }

        Usuario usuario = usuarioDAO.obtenerPorCorreo(correo);

        if (usuario == null) {
            throw new Exception("Credenciales inválidas");
        }

        // Verificar contraseña con BCrypt
        if (!BCrypt.checkpw(contrasena, usuario.getContrasena())) {
            throw new Exception("Credenciales inválidas");
        }

        return usuario;
    }

    /**
     * Validar si el usuario es administrador
     */
    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && usuario.getRol() == RolUsuario.ADMINISTRADOR;
    }

    /**
     * Registrar nuevo usuario
     */
    public Usuario registrar(Usuario usuario) throws Exception {
        // Validaciones pa la contra
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo es requerido");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es requerido");
        }

        if (!usuario.getCorreo().matches("^[A-Za-z0-9+_.-]+@(.+)$")) {
            throw new Exception("Formato de correo inválido");
        }

        Usuario existente = usuarioDAO.obtenerPorCorreo(usuario.getCorreo());
        if (existente != null) {
            throw new Exception("El correo ya está registrado");
        }

        String hashedPassword = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt(12));
        usuario.setContrasena(hashedPassword);

        if (usuario.getRol() == null) {
            usuario.setRol(RolUsuario.CLIENTE);
        }

        return usuarioDAO.guardar(usuario);
    }

    /**
     * Obtener todos los usuarios
     */
    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    /**
     * Obtener usuario por ID
     */
    public Usuario obtenerPorId(int id) {
        return usuarioDAO.obtenerPorId(id);
    }

    /**
     * Actualizar usuario
     */
    public Usuario actualizar(Usuario usuario) throws Exception {
        Usuario existente = usuarioDAO.obtenerPorId(usuario.getId());
        if (existente == null) {
            throw new Exception("Usuario no encontrado");
        }

        // Si la contraseña está vacía, mantener la anterior
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            usuario.setContrasena(existente.getContrasena());
        } else {
            // Si hay nueva contraseña, hashearla
            String hashedPassword = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt(12));
            usuario.setContrasena(hashedPassword);
        }

        return usuarioDAO.actualizar(usuario);
    }

    /**
     * Cambiar contraseña
     */
    public void cambiarContrasena(int usuarioId, String contrasenaActual, String nuevaContrasena) throws Exception {
        Usuario usuario = usuarioDAO.obtenerPorId(usuarioId);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }

        // Verificar contraseña actual
        if (!BCrypt.checkpw(contrasenaActual, usuario.getContrasena())) {
            throw new Exception("La contraseña actual es incorrecta");
        }

        // Validar nueva contraseña
        if (nuevaContrasena == null || nuevaContrasena.length() < 6) {
            throw new Exception("La nueva contraseña debe tener al menos 6 caracteres");
        }

        // Hashear y guardar nueva contraseña
        String hashedPassword = BCrypt.hashpw(nuevaContrasena, BCrypt.gensalt(12));
        usuario.setContrasena(hashedPassword);
        usuarioDAO.actualizar(usuario);
    }

    /**
     * Eliminar usuario
     */
    public void eliminar(int id) throws Exception {
        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            throw new Exception("Usuario no encontrado");
        }
        usuarioDAO.eliminar(id);
    }
}
