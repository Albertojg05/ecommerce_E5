/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 * Click nbfs://nbhost/SystemFileSystem/Templates/Classes/Class.java to edit this template
 */
package com.mycompany.ecommerce_e5.bo;

import com.mycompany.ecommerce_e5.dao.UsuarioDAO;
import com.mycompany.ecommerce_e5.dominio.Usuario;
import com.mycompany.ecommerce_e5.dominio.enums.RolUsuario;
import com.mycompany.ecommerce_e5.excepciones.AutenticacionException;
import com.mycompany.ecommerce_e5.excepciones.BusinessException;
import com.mycompany.ecommerce_e5.excepciones.RecursoNoEncontradoException;
import com.mycompany.ecommerce_e5.excepciones.ValidacionException;
import com.mycompany.ecommerce_e5.util.Messages;
import com.mycompany.ecommerce_e5.util.ValidationUtil;
import org.mindrot.jbcrypt.BCrypt;
import java.util.List;

/**
 * Clase BO (Business Object) para la logica de negocio de usuarios.
 * Contiene las reglas de negocio para registro, login, actualizacion
 * y validacion de usuarios. Usa BCrypt para encriptar contrasenas
 * y garantizar la seguridad de los datos.
 *
 * @author Alberto Jiménez García 252595
 * @author Rene Ezequiel Figueroa Lopez 228691
 * @author Freddy Alí Castro Román 252191
 */
public class UsuarioBO {

    private final UsuarioDAO usuarioDAO;

    public UsuarioBO() {
        this.usuarioDAO = new UsuarioDAO();
    }

    public List<Usuario> obtenerTodos() {
        return usuarioDAO.obtenerTodos();
    }

    public Usuario obtenerPorId(int id) throws BusinessException {
        if (id <= 0) {
            throw new ValidacionException("id", "El ID del usuario debe ser mayor a 0");
        }

        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario", id);
        }

        return usuario;
    }

    /**
     * Busca un usuario por su correo electrónico.
     * @param correo El correo a buscar
     * @return El usuario encontrado o null si no existe
     */
    public Usuario buscarPorCorreo(String correo) {
        if (correo == null || correo.trim().isEmpty()) {
            return null;
        }
        return usuarioDAO.obtenerPorCorreo(correo.trim().toLowerCase());
    }

    public Usuario registrar(Usuario usuario) throws BusinessException {
        validarDatosUsuario(usuario);

        String correoNormalizado = usuario.getCorreo().trim().toLowerCase();
        Usuario usuarioExistente = usuarioDAO.obtenerPorCorreo(correoNormalizado);
        if (usuarioExistente != null) {
            throw new ValidacionException("correo", Messages.ERROR_CORREO_DUPLICADO);
        }

        usuario.setCorreo(correoNormalizado);

        String contrasenaHasheada = BCrypt.hashpw(usuario.getContrasena(), BCrypt.gensalt(12));
        usuario.setContrasena(contrasenaHasheada);

        if (usuario.getRol() == null) {
            usuario.setRol(RolUsuario.CLIENTE);
        }

        return usuarioDAO.guardar(usuario);
    }

    public Usuario login(String correo, String contrasena) throws BusinessException {
        if (!ValidationUtil.isNotEmpty(correo) || !ValidationUtil.isNotEmpty(contrasena)) {
            throw new AutenticacionException(Messages.ERROR_CREDENCIALES_INVALIDAS);
        }

        String correoNormalizado = correo.trim().toLowerCase();
        Usuario usuario = usuarioDAO.obtenerPorCorreo(correoNormalizado);

        if (usuario == null) {
            throw new AutenticacionException(Messages.ERROR_CREDENCIALES_INVALIDAS);
        }

        if (!BCrypt.checkpw(contrasena, usuario.getContrasena())) {
            throw new AutenticacionException(Messages.ERROR_CREDENCIALES_INVALIDAS);
        }

        return usuario;
    }

    public Usuario actualizar(Usuario usuario) throws BusinessException {
        if (usuario == null || usuario.getId() <= 0) {
            throw new ValidacionException("Los parámetros del usuario no son válidos");
        }

        Usuario usuarioExistente = usuarioDAO.obtenerPorId(usuario.getId());
        if (usuarioExistente == null) {
            throw new RecursoNoEncontradoException("Usuario", usuario.getId());
        }

        if (!ValidationUtil.isNotEmpty(usuario.getNombre())) {
            throw new ValidacionException("nombre", "El nombre es requerido");
        }

        if (!ValidationUtil.isValidEmail(usuario.getCorreo())) {
            throw new ValidacionException("correo", Messages.ERROR_CORREO_INVALIDO);
        }

        String correoNormalizado = usuario.getCorreo().trim().toLowerCase();
        Usuario usuarioPorCorreo = usuarioDAO.obtenerPorCorreo(correoNormalizado);
        if (usuarioPorCorreo != null && usuarioPorCorreo.getId() != usuario.getId()) {
            throw new ValidacionException("correo", Messages.ERROR_CORREO_DUPLICADO);
        }

        usuario.setCorreo(correoNormalizado);

        return usuarioDAO.actualizar(usuario);
    }

    public void cambiarContrasena(int usuarioId, String contrasenaActual, String contrasenaNueva)
            throws BusinessException {

        if (usuarioId <= 0) {
            throw new ValidacionException("id", "El ID del usuario debe ser mayor a 0");
        }

        Usuario usuario = usuarioDAO.obtenerPorId(usuarioId);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario", usuarioId);
        }

        if (!BCrypt.checkpw(contrasenaActual, usuario.getContrasena())) {
            throw new AutenticacionException("La contraseña actual es incorrecta");
        }

        if (!ValidationUtil.hasMinLength(contrasenaNueva, 6)) {
            throw new ValidacionException("contrasenaNueva", Messages.ERROR_CONTRASENA_CORTA);
        }

        String nuevaContrasenaHasheada = BCrypt.hashpw(contrasenaNueva, BCrypt.gensalt(12));
        usuario.setContrasena(nuevaContrasenaHasheada);

        usuarioDAO.actualizar(usuario);
    }

    public boolean esAdministrador(Usuario usuario) {
        return usuario != null && usuario.getRol() == RolUsuario.ADMINISTRADOR;
    }

    public void eliminar(int id) throws BusinessException {
        if (id <= 0) {
            throw new ValidacionException("id", "El ID del usuario debe ser mayor a 0");
        }

        Usuario usuario = usuarioDAO.obtenerPorId(id);
        if (usuario == null) {
            throw new RecursoNoEncontradoException("Usuario", id);
        }

        usuarioDAO.eliminar(id);
    }

    private void validarDatosUsuario(Usuario usuario) throws ValidacionException {
        if (usuario == null) {
            throw new ValidacionException("El usuario no puede ser nulo");
        }

        if (!ValidationUtil.isNotEmpty(usuario.getNombre())) {
            throw new ValidacionException("nombre", "El nombre es requerido");
        }

        if (usuario.getNombre().trim().length() < 2) {
            throw new ValidacionException("nombre", "El nombre debe tener al menos 2 caracteres");
        }

        if (!ValidationUtil.isValidEmail(usuario.getCorreo())) {
            throw new ValidacionException("correo", Messages.ERROR_CORREO_INVALIDO);
        }

        if (!ValidationUtil.hasMinLength(usuario.getContrasena(), 6)) {
            throw new ValidacionException("contrasena", Messages.ERROR_CONTRASENA_CORTA);
        }
    }
}
