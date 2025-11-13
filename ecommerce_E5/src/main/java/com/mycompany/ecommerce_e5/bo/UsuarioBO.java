/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.UsuarioDAO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.dominio.enums.RolUsuario;
import java.util.List;

/**
 *
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
     * Validar login de usuario
     */
    public Usuario login(String correo, String contrasena) throws Exception {
        if (correo == null || correo.trim().isEmpty()) {
            throw new Exception("El correo es requerido");
        }
        if (contrasena == null || contrasena.trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        
        Usuario usuario = usuarioDAO.validarLogin(correo, contrasena);
        if (usuario == null) {
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
        // Vaalidaciones
        if (usuario.getCorreo() == null || usuario.getCorreo().trim().isEmpty()) {
            throw new Exception("El correo es requerido");
        }
        if (usuario.getContrasena() == null || usuario.getContrasena().trim().isEmpty()) {
            throw new Exception("La contraseña es requerida");
        }
        if (usuario.getNombre() == null || usuario.getNombre().trim().isEmpty()) {
            throw new Exception("El nombre es requerido");
        }
        
        // Verificar si el correo ya existe
        Usuario existente = usuarioDAO.obtenerPorCorreo(usuario.getCorreo());
        if (existente != null) {
            throw new Exception("El correo ya está registrado");
        }
        
        // Asignar rol por defecto
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
        return usuarioDAO.actualizar(usuario);
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
